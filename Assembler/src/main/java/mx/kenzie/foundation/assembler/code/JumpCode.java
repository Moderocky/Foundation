package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.valross.constantine.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.BiFunction;

/**
 * An opcode for jumping to a branch.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record JumpCode(String mnemonic, byte code, BiFunction<Branch, Byte, JumpInstruction> function)
    implements OpCode {

    //<editor-fold desc="Field Reference" defaultstate="collapsed">
    public CodeElement jump(Branch branch) {
        return function.apply(branch, code);
    }

    public CodeElement jump(int bytes) {
        return CodeElement.vector(code, U2.valueOf((short) bytes));
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

    public record Goto(Branch target, byte code) implements JumpInstruction {

        @Override
        public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
            stream.write(code);
            this.target.getJump(this).write(stream);
        }

        @Override
        public void notify(CodeBuilder builder) {
            JumpInstruction.super.notify(builder);
            builder.stack().reframe();
            builder.write(new Branch.UnconditionalBranch()); // goto is an unconditional jump, we need an implicit
            // branch here
        }

        @Override
        public int length() {
            return 3;
        }

        @Override
        public Constant constant() {
            return UVec.of(this.binary());
        }

    }

}
