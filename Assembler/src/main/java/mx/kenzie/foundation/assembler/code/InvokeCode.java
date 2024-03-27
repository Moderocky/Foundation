package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.detail.Member;
import mx.kenzie.foundation.detail.Type;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
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

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement constructor(Klass owner,
                                                                                                      Klass... parameters) {
        return this.method(new Member(owner, void.class, "<init>", parameters));
    }

    public UnboundedElement method(Member member) {
        int taken = Type.parameterSize(member);
        if (code != Codes.INVOKESTATIC) ++taken; // caller obj is first 1-wide "parameter"
        if (taken == 0) taken += Type.fromDescriptor(member).width(); // it might be a 0-args method returning wide type
        final int count = taken;
        return storage -> new Invocation(code, storage.constant(member), count);
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
    }

    @Override
    public int length() {
        return 3;
    }

    private record Invocation(byte code, PoolReference reference, int width) implements CodeElement, RecordConstant {

        @Override
        public int length() {
            return 3;
        }

        @Override
        public void write(OutputStream stream) throws IOException {
            stream.write(code);
            this.reference.write(stream);
        }

        @Override
        public void notify(CodeBuilder builder) {
            builder.notifyStack(width);
        }

    }

}
