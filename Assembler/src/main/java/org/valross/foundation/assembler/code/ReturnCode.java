package org.valross.foundation.assembler.code;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.ProgramRegister;
import org.valross.foundation.assembler.tool.ProgramStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A simple, single-byte instruction, containing only its operation code.
 * Since no additional parameters are needed for this instruction it may be used as-is.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record ReturnCode(String mnemonic, byte code, Consumer<CodeBuilder> notifier)
        implements RecordConstant, SingleInstruction {

    public ReturnCode(String mnemonic, int code, BiConsumer<ProgramStack, ProgramRegister> notifier) {
        this(mnemonic, (byte) code, builder -> {
            if (builder.trackStack()) notifier.accept(builder.stack(), builder.register());
        });
    }

    public CodeElement type(Class<?> type) {
        if (type == void.class) return RETURN;
        if (type == int.class) return IRETURN;
        if (type == boolean.class) return IRETURN;
        if (type == byte.class) return IRETURN;
        if (type == short.class) return IRETURN;
        if (type == long.class) return LRETURN;
        if (type == float.class) return FRETURN;
        if (type == double.class) return DRETURN;
        return ARETURN;
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
