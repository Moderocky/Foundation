package org.valross.foundation.assembler.code;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.tool.ProgramStack;
import org.valross.foundation.detail.Member;
import org.valross.foundation.detail.Type;
import org.valross.foundation.detail.TypeHint;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.TypeDescriptor;
import java.util.Arrays;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.INTERFACE_METHOD_REFERENCE;

/**
 * An opcode for invoking a method (via invokevirtual, invokestatic or invokespecial).
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record InvokeCode(String mnemonic, byte code) implements OpCode, AbstractInvokeCode {

    public CodeElement method(PoolReference reference) {
        return CodeElement.vector(code, reference);
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement constructor(Klass owner,
                                                                                                      Klass... parameters) {
        return this.method(new Member(owner, void.class, "<init>", parameters));
    }

    @Override
    public UnboundedElement method(Member member) {
        int taken = Type.parameterSize(member);
        if (code != Codes.INVOKESTATIC) ++taken; // caller obj is first 1-wide "parameter"
        if (taken == 0) taken += Type.fromDescriptor(member).width(); // it might be a 0-args method returning wide type
        final int count = taken;
        return storage -> new Invocation(code, member, storage.constant(member), count);
    }

    @Override
    public UnboundedElement interfaceMethod(Member member) {
        int taken = Type.parameterSize(member);
        if (code != Codes.INVOKESTATIC) ++taken; // caller obj is first 1-wide "parameter"
        if (taken == 0) taken += Type.fromDescriptor(member).width(); // it might be a 0-args method returning wide type
        final int count = taken;
        return storage -> new Invocation(code, member, storage.constant(INTERFACE_METHOD_REFERENCE, member), count);
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
    }

    @Override
    public int length() {
        return 3;
    }

    private record Invocation(byte code, Member member, PoolReference reference, int width)
        implements CodeElement, RecordConstant {


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
            if (!builder.trackStack()) return;
            final ProgramStack stack = builder.stack();
            for (int i = member.parameters().length - 1; i >= 0; i--) stack.pop(member.parameters()[i].width());
            switch (code) {
                case Codes.INVOKESTATIC:
                    break;
                case Codes.INVOKESPECIAL: // initialise the duplicate in memory
                    if (member.name().equals("<init>")) {
                        final TypeHint peek = stack.peek();
                        assert peek instanceof TypeHint.InitialisableType thing && !thing.isInitialisedType();
                        if (peek instanceof TypeHint.InitialisableType thing) thing.initialise();
                    }
                default:
                    stack.pop();
            }
            if (member.returnType().width() > 0) stack.push(member.returnType());
        }

        @Override
        public String toString() {
            return OpCode.getCode(Byte.toUnsignedInt(code)).mnemonic().toLowerCase() + "/" + Byte.toUnsignedInt(code)
                + ": " + member.owner().getSimpleName() + "." + member.name()
                + '('
                + (String.join(", ", Arrays.stream(member.parameters()).map(Type::getSimpleName).toList()))
                + ')';
        }

    }

}

