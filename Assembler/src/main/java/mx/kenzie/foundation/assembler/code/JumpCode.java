package mx.kenzie.foundation.assembler.code;

/**
 * An opcode for jumping to a branch.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record JumpCode(String mnemonic, byte code) implements OpCode {

    //<editor-fold desc="Field Reference" defaultstate="collapsed">
    public CodeElement jump(Branch branch) {
        return CodeElement.vector(code, branch.getHandle());
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
