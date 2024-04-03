package org.valross.foundation.assembler.code;

/**
 * An instruction for widening another instruction.
 * This can be unreliable to use -- most instructions that support widening have support built in.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record WideCode(String mnemonic, byte code) implements OpCode {

    //<editor-fold desc="WIDE" defaultstate="collapsed">
    @Override
    public int length() {
        return 6;
    }

    public UnboundedElement widen(CodeElement instruction) {
        return CodeElement.wide(instruction);
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
    }
    //</editor-fold>

}
