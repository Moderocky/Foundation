package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.detail.Member;

import java.lang.invoke.TypeDescriptor;

/**
 * An opcode for invoking a method (via invokevirtual, invokestatic or invokespecial).
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record InvokeCode(String mnemonic, byte code) implements OpCode {

    public CodeElement method(PoolReference reference) {
        return CodeElement.vector(code, reference);
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement method(Klass owner,
                                                                                                 Klass returnType,
                                                                                                 String name,
                                                                                                 Klass... parameters) {
        return this.method(new Member(owner, returnType, name, parameters));
    }

    public UnboundedElement method(Member member) {
        return storage -> CodeElement.vector(code, storage.constant(member));
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
    }

    @Override
    public int length() {
        return 3;
    }

}
