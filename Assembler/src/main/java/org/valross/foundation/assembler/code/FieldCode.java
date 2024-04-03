package org.valross.foundation.assembler.code;

import org.valross.foundation.assembler.constant.ConstantPoolInfo;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.detail.Erasure;
import org.valross.foundation.detail.Member;

import java.lang.invoke.TypeDescriptor;
import java.util.function.Consumer;

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
        final Consumer<CodeBuilder> notifier = switch (code) {
            case Codes.GETFIELD -> builder -> {
                builder.stack().pop();
                builder.stack().push(member.returnType());
            };
            case Codes.GETSTATIC -> builder -> builder.stack().push(member.returnType());
            case Codes.PUTFIELD -> builder -> {
                builder.stack().pop(member.returnType());
                builder.stack().pop();
            };
            case Codes.PUTSTATIC -> builder -> builder.stack().pop(member.returnType());
            default -> null;
        };
        return storage -> CodeElement.notify(this.field(storage.constant(ConstantPoolInfo.FIELD_REFERENCE, member)),
                                             notifier);
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
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
    }

    @Override
    public int length() {
        return 3;
    }
    //</editor-fold>

}
