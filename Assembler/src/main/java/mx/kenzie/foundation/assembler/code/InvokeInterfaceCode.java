package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.assembler.vector.U1;
import mx.kenzie.foundation.assembler.vector.UVec;
import mx.kenzie.foundation.detail.Member;
import mx.kenzie.foundation.detail.Type;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.TypeDescriptor;

/**
 * An opcode for invoking a method (via invokeinterface).
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record InvokeInterfaceCode(String mnemonic, byte code) implements OpCode {

    /**
     * Invokes the interface method in the specified constant pool index.
     *
     * @param reference The constant method reference
     * @param count     The total size of the arguments being used, including the calling object
     * @return A method invocation instruction
     */
    public CodeElement method(PoolReference reference, int count) {
        return CodeElement.vector(code, UVec.of(reference, U1.valueOf(count), U1.ZERO));
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement method(Klass owner,
                                                                                                 Klass returnType,
                                                                                                 String name,
                                                                                                 Klass... parameters) {
        int width = 1;
        for (Klass parameter : parameters) width += Type.of(parameter).width();
        final Member member = new Member(owner, returnType, name, parameters);
        final int count = width;
        return storage -> this.method(storage.constant(member), count);
    }

    public UnboundedElement method(Member member) {
        final int count = Type.parameterSize(member) + 1;
        final int width = count - Type.fromDescriptor(member).width(); // we're returning something
        return storage -> new Invocation(code, storage.constant(member), count, width);
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
    }

    @Override
    public int length() {
        return 5;
    }

    private record Invocation(byte code, PoolReference reference, int count, int width)
        implements CodeElement, RecordConstant {

        @Override
        public int length() {
            return 5;
        }

        @Override
        public void write(OutputStream stream) throws IOException {
            stream.write(code);
            this.reference.write(stream);
            U1.valueOf(count).write(stream);
            stream.write(0);
        }

        @Override
        public void notify(CodeBuilder builder) {
            builder.notifyStack(width);
        }

    }

}
