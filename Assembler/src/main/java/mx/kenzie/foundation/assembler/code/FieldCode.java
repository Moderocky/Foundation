package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.PoolReference;

/**
 * An opcode for accessing a field (e.g. getting/setting value)
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record FieldCode(String mnemonic, byte code) implements OpCode {

    //<editor-fold desc="Field Reference" defaultstate="collapsed">
    public CodeElement field(PoolReference fieldReference) {
        return CodeElement.vector(code, fieldReference);
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
    }

    @Override
    public int length() {
        return 3;
    }
    //</editor-fold>

}
