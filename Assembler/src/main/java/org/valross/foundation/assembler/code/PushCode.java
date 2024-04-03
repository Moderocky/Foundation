package org.valross.foundation.assembler.code;

import org.valross.foundation.assembler.tool.StackNotifier;
import org.valross.foundation.detail.Type;

/**
 * An opcode for pushing a byte or short value (e.g. bipush, sipush)
 * This will actually default to the smallest code unless one type's value is specified.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record PushCode(String mnemonic, byte code) implements OpCode {

    //<editor-fold desc="Push value" defaultstate="collapsed">
    public CodeElement value(byte value) {
        return CodeElement.notify(CodeElement.fixed(Codes.BIPUSH, value), StackNotifier.push(Type.INT));
    }

    public CodeElement value(short value) {
        return CodeElement.notify(CodeElement.fixed(Codes.SIPUSH, (byte) (value >> 8), (byte) (value)),
                                  StackNotifier.push(Type.INT));
    }

    public CodeElement value(int value) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE)
            throw new IllegalArgumentException("Use LDC for constants outside the short max/min values.");
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) return this.value((short) value);
        else return this.value((byte) value);
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
    }

    @Override
    public int length() {
        return code == Codes.SIPUSH ? 3 : 2;
    }
    //</editor-fold>

}
