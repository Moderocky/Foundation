package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.valross.constantine.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * An opcode for jumping to a branch.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record JumpCode(String mnemonic, byte code, Consumer<CodeBuilder> notifier)
    implements OpCode {

    //<editor-fold desc="Jump" defaultstate="collapsed">
    public CodeElement jump(Branch branch) {
        return new JumpInstruction() {
            @Override
            public byte code() {
                return code;
            }

            @Override
            public int length() {
                return 3;
            }

            @Override
            public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
                stream.write(code);
                branch.getJump(this).write(stream);
            }

            @Override
            public Constant constant() {
                return UVec.of(this.binary());
            }

            @Override
            public Branch target() {
                return branch;
            }

            @Override
            public void notify(CodeBuilder builder) {
                notifier.accept(builder);
                if (code != Codes.GOTO) return;
                if (builder.trackStack()) builder.stack().reframe();
            }

            @Override
            public void insert(CodeBuilder builder) {
                JumpInstruction.super.insert(builder);
                if (code != Codes.GOTO) return;
                if (!builder.trackFrames()) return;
                builder.write(new Branch.UnconditionalBranch()); // goto is an unconditional jump, we need an implicit
                // branch here
            }
        };
    }

    public CodeElement jump(int bytes) {
        return CodeElement.notify(CodeElement.vector(code, U2.valueOf((short) bytes)), notifier);
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
    }

    @Override
    public int length() {
        return 3;
    }
    //</editor-fold>

    public interface JumpInstruction extends CodeElement {

        Branch target();

        @Override
        default void notify(CodeBuilder builder) {
            CodeElement.super.notify(builder);
            if (!builder.trackStack()) return;
            this.target().checkFrame(builder.stack(), builder.register());
        }

    }

}