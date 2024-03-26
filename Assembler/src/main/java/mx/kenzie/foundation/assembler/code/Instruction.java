package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * A simple, single-byte instruction, containing only its operation code.
 * Since no additional parameters are needed for this instruction it may be used as-is.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record Instruction(String mnemonic, byte code, Consumer<CodeBuilder> notifier)
    implements OpCode, CodeElement, RecordConstant {

    public Instruction(String mnemonic, int code) {
        this(mnemonic, (byte) code, null);
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public byte[] binary() {
        return new byte[] {code};
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        stream.write(code);
    }

    @Override
    public void notify(CodeBuilder builder) {
        if (notifier != null) notifier.accept(builder);
        else CodeElement.super.notify(builder);
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
    }

}
