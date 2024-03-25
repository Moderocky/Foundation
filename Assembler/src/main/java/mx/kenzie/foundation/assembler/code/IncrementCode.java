package mx.kenzie.foundation.assembler.code;

/**
 * An instruction for incrementing the numeric value in a variable slot.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record IncrementCode(String mnemonic, byte code) implements OpCode {

    //<editor-fold desc="IINC" defaultstate="collapsed">
    @Override
    public int length() {
        return 3;
    }

    public UnboundedElement var(int slot, int increment) {
        final boolean wide = slot > 255 || increment > Byte.MAX_VALUE || increment < Byte.MIN_VALUE;
        if (wide)
            return CodeElement.incrementStack(CodeElement.wide(CodeElement.fixed(code, (byte) (slot >>> 8),
                (byte) (slot), (byte) (increment >> 8), (byte) (increment))), 0);
        return CodeElement.fixed(this.code(), (byte) slot, (byte) increment);
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
    }
    //</editor-fold>

}
