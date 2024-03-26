package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.constant.ConstantPoolInfo;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.detail.Erasure;
import mx.kenzie.foundation.detail.Member;

import java.lang.invoke.TypeDescriptor;

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

    public UnboundedElement field(Member member) {
        return storage -> this.field(storage.constant(ConstantPoolInfo.FIELD_REFERENCE, member));
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement field(Klass owner, String name,
                                                                                          Klass type) {
        return this.field(new Member(owner, name, type));
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement field(Klass owner,
                                                                                          Erasure erasure) {
        return this.field(new Member(owner, erasure));
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
