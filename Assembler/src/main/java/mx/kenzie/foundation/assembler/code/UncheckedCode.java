package mx.kenzie.foundation.assembler.code;

/**
 * An opcode reference that doesn't have a strictly-defined type or data schema.
 * This is most likely a reserved/internal opcode, and/or one that is used by a specific VM/debugger.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 * @param length   The (expected) length of this instruction, including 1 for the code itself.
 *                 If the length is indeterminable (i.e. a conditional or variable-length instruction) then
 *                 this should return -1.
 */
public record UncheckedCode(String mnemonic, byte code, int length) implements OpCode {

    //<editor-fold desc="An unchecked code, with nothing known about it.">
    public UncheckedCode(String mnemonic, byte code) {
        this(mnemonic, code, -1);
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
    }
    //</editor-fold>

}
