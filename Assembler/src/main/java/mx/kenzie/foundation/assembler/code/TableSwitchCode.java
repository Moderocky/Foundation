package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.tool.InstructionReference;
import mx.kenzie.foundation.assembler.vector.U4;
import mx.kenzie.foundation.detail.Type;
import mx.kenzie.foundation.detail.TypeHint;
import org.valross.constantine.Array;
import org.valross.constantine.Constant;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

/**
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record TableSwitchCode(String mnemonic, byte code) implements RecordConstant, OpCode {

    /**
     * A table switch consumes an int, and then jumps to branch {@code input - start}.
     * Branches are indexed from 0, where branch 0 is jumped to when {@code input == start}
     * If the input is less than the start, or the input would jump past the last branch,
     * it jumps to the default branch instead.
     *
     * @param defaultCase The `default` case of the switch table
     * @param start       The factor to remove from the int before jumping
     * @param branches    The branches to jump to, in order.
     * @return A switch instruction.
     */
    public TableSwitch test(Branch defaultCase, int start, Branch... branches) {
        return new TableSwitch(defaultCase, start, start + (branches.length - 1), branches);
    }

    @Override
    public int length() {
        return -1;
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
    }

    public class TableSwitch implements CodeElement, Constant {

        protected final Branch defaultCase;
        protected final int low, high;
        private final Branch[] branches;
        protected transient InstructionReference reference;

        public TableSwitch(Branch defaultCase, int low, int high, Branch... branches) {
            this.defaultCase = defaultCase;
            this.branches = branches;
            this.low = low;
            this.high = high;
            assert branches.length == (high - low) + 1;
        }

        private int padding() {
            if (reference != null) return 4 - ((1 + reference.index()) % 4);
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
                assert key.equals(Type.INT) : "Expected table switch index to be an int, found a " + key;
            }
            CodeElement.super.notify(builder);
        }

        @Override
        public byte code() {
            return code;
        }

        @Override
        public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
            stream.write(code);
            for (int i = 0; i < this.padding(); i++) stream.write(Codes.NOP);
            this.defaultCase.getWideJump(this).write(stream);
            U4.fromSigned(low).write(stream);
            U4.fromSigned(high).write(stream);
            for (Branch branch : branches) branch.getWideJump(this).write(stream);
        }

        @Override
        public int length() {
            return 1 + this.padding() + 4 + 4 + 4 + (branches.length * 4);
        }

        @Override
        public Constable[] serial() {
            return new Constable[] {defaultCase, low, high, new Array(branches)};
        }

        @Override
        public Class<?>[] canonicalParameters() {
            return new Class[] {Branch.class, Branch[].class};
        }

    }

}
