package org.valross.foundation.assembler.code;

import org.valross.constantine.Array;
import org.valross.constantine.Constant;
import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.InstructionReference;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.foundation.detail.Type;
import org.valross.foundation.detail.TypeHint;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record LookupSwitchCode(String mnemonic, byte code) implements RecordConstant, OpCode {

    public Builder test(Branch defaultCase) {
        return new Builder(defaultCase);
    }

    public LookupSwitch test(Branch defaultCase, Match... branches) {
        return new LookupSwitch(defaultCase, branches);
    }

    @Override
    public int length() {
        return -1;
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
    }

    public record Match(int value, Branch branch) implements RecordConstant {

    }

    public class LookupSwitch implements CodeElement, Constant {

        protected final Branch defaultCase;
        private final Case[] cases;
        protected transient InstructionReference reference;

        public LookupSwitch(Branch defaultCase, Match... matches) {
            this.defaultCase = defaultCase;
            this.cases = new Case[matches.length];
            for (int i = 0; i < matches.length; i++) cases[i] = new Case(matches[i]);
            Arrays.sort(cases, Comparator.comparing(Case::getValue));
        }

        private int padding() {
            if (reference != null) {
                int offset = (1 + reference.index()) % 4;
                if (offset == 0) return 0;
                return 4 - offset;
            }
            return 0;
        }

        @Override
        public void insert(CodeBuilder builder) {
            this.reference = new InstructionReference(builder.vector(), this);
            CodeElement.super.insert(builder);
        }

        @Override
        public void notify(CodeBuilder builder) {
            if (builder.trackStack()) {
                final TypeHint key = builder.stack().pop();
                assert key.equals(Type.INT) : "Expected lookup switch key to be an int, found a " + key;
                for (Case aCase : cases) {
                    aCase.branch.checkFrame(builder.stack(), builder.register());
                }
                this.defaultCase.checkFrame(builder.stack(), builder.register());
                builder.stack().reframe();
                builder.register().reframe();
            }
            CodeElement.super.notify(builder);
        }

        @Override
        public byte code() {
            return code;
        }

        @Override
        public void write(OutputStream stream) throws IOException {
            stream.write(code);
            for (int i = 0; i < this.padding(); i++) stream.write(Codes.NOP);
            this.defaultCase.getWideJump(this).write(stream);
            U4.fromSigned(cases.length).write(stream);
            for (Case match : cases) match.write(stream);
        }

        @Override
        public int length() {
            return 1 + this.padding() + 8 + (cases.length * 8);
        }

        @Override
        public Constable[] serial() {
            return new Constable[] {defaultCase, new Array(cases)};
        }

        @Override
        public Class<?>[] canonicalParameters() {
            return new Class[] {Branch.class, Match[].class};
        }

        protected class Case implements UVec {

            protected final int value;
            protected final Branch branch;

            public Case(int value, Branch branch) {
                this.value = value;
                this.branch = branch;
            }

            public Case(Match match) {
                this(match.value, match.branch);
            }

            public int getValue() {
                return value;
            }

            @Override
            public int length() {
                return 8;
            }

            @Override
            public void write(OutputStream stream) throws IOException {
                U4.fromSigned(value).write(stream);
                this.branch.getWideJump(LookupSwitch.this).write(stream);
            }

            @Override
            public Constant constant() {
                return UVec.of(this.binary());
            }

        }

    }

    public class Builder implements UnboundedElement {

        Branch defaultCase;
        final List<Match> matches;

        public Builder(Branch defaultCase) {
            this.defaultCase = defaultCase;
            this.matches = new ArrayList<>(8);
        }

        public Builder() {
            this.matches = new ArrayList<>(8);
        }

        public Builder test(int match, Branch target) {
            this.matches.add(new Match(match, target));
            return this;
        }

        public Builder test(Match match) {
            this.matches.add(match);
            return this;
        }

        public Builder defaultCase(Branch defaultCase) {
            this.defaultCase = defaultCase;
            return this;
        }

        public LookupSwitch build() {
            return LookupSwitchCode.this.test(defaultCase, matches.toArray(new Match[0]));
        }

        @Override
        public CodeElement bound(ClassFileBuilder.Storage storage) {
            return LookupSwitchCode.this.test(defaultCase, matches.toArray(new Match[0]));
        }

    }

}
