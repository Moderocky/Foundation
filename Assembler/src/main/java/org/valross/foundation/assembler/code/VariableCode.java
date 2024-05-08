package org.valross.foundation.assembler.code;

import org.valross.foundation.assembler.tool.StackNotifier;
import org.valross.foundation.assembler.vector.U2;

/**
 * A variable loading/storing code. This has its own special handlers so that it can
 * default to the built-in instructions (e.g. `aload 0` -> `aload_0`) to save space
 * wherever possible.
 */
public abstract class VariableCode implements OpCode {

    //<editor-fold desc="Variable" defaultstate="collapsed">
    private final String mnemonic;
    private final byte code;

    public VariableCode(String mnemonic, byte code) {
        this.mnemonic = mnemonic;
        this.code = code;
    }

    public VariableCode(String mnemonic, int code) {
        this(mnemonic, (byte) code);
    }

    @Override
    public String mnemonic() {
        return mnemonic;
    }

    @Override
    public byte code() {
        return code;
    }

    @Override
    public int length() {
        return 2;
    }

    public CodeElement var(Class<?> type, int slot) {
        if (code < 54) { // 54 = ISTORE :)
            if (type == int.class || type == boolean.class
                || type == char.class || type == short.class || type == byte.class) return ILOAD.var(slot);
            if (type == float.class) return FLOAD.var(slot);
            if (type == long.class) return LLOAD.var(slot);
            if (type == double.class) return FLOAD.var(slot);
            return ALOAD.var(slot);
        } else {
            if (type == int.class || type == boolean.class
                || type == char.class || type == short.class || type == byte.class) return ISTORE.var(slot);
            if (type == float.class) return FSTORE.var(slot);
            if (type == long.class) return LSTORE.var(slot);
            if (type == double.class) return FSTORE.var(slot);
            return ASTORE.var(slot);
        }
    }

    public CodeElement var(int slot) {
        U2.valueOf(slot);
        final int increment = switch (code) {
            case Codes.LLOAD, Codes.DLOAD -> 2;
            case Codes.LSTORE, Codes.DSTORE -> -2;
            default -> code < Codes.ISTORE ? 1 : -1;
        };
        if (slot > 255)
            return increment > 0 ? CodeElement.notify(CodeElement.wide(CodeElement.fixed(code, (byte) (slot >>> 8),
                    (byte) (slot))),
                StackNotifier.pushVariable(slot)) :
                CodeElement.notify(CodeElement.wide(CodeElement.fixed(code, (byte) (slot >>> 8), (byte) (slot))),
                    builder -> builder.register()
                        .put(slot, builder.stack()
                            .popSafe()));
        return increment > 0 ? CodeElement.notify(CodeElement.fixed(code, (byte) slot),
            StackNotifier.pushVariable(slot)) :
            CodeElement.notify(CodeElement.fixed(code, (byte) slot), builder -> builder.register()
                .put(slot, builder.stack()
                    .popSafe()));
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
    }
    //</editor-fold>

}
