package org.valross.foundation.assembler.code;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.ProgramRegister;
import org.valross.foundation.assembler.tool.ProgramStack;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

interface SingleInstruction extends OpCode, CodeElement {

    default byte[] binary() {
        return new byte[] {this.code()};
    }

    default void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        stream.write(this.code());
    }

    byte code();

    default int length() {
        return 1;
    }

}

/**
 * A simple, single-byte instruction, containing only its operation code.
 * Since no additional parameters are needed for this instruction it may be used as-is.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record Instruction(String mnemonic, byte code, Consumer<CodeBuilder> notifier)
    implements RecordConstant, SingleInstruction {

    public Instruction(String mnemonic, int code, BiConsumer<ProgramStack, ProgramRegister> notifier) {
        this(mnemonic, (byte) code, builder -> {
            if (builder.trackStack()) notifier.accept(builder.stack(), builder.register());
        });
    }

    public Instruction(String mnemonic, int code) {
        this(mnemonic, (byte) code, (Consumer<CodeBuilder>) null);
    }

    @Override
    public void notify(CodeBuilder builder) {
        if (notifier != null) notifier.accept(builder);
        else SingleInstruction.super.notify(builder);
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
    }

}
