package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Type;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.TypeDescriptor;

public class CallSuper {

    CallSuper() {
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass owner, Klass returnType, String name, Klass... parameters) {
        return new Stub(Type.of(owner), Type.of(returnType), name, Type.array(parameters));
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass owner, Klass... parameters) {
        return new Stub(Type.of(owner), Type.of(void.class), "<init>", Type.array(parameters));
    }

    public record Stub(Type owner, Type returnType, String name, Type... parameters) {

        public Instruction.Base call(Instruction.Input<Object> object, Instruction.Input<?>... arguments) {
            return visitor -> {
                object.write(visitor);
                for (Instruction.Input<?> argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), false);
                if (returnType != Type.VOID) visitor.visitInsn(Opcodes.POP);
            };
        }

        public <Result> Instruction.Input<Result> get(Instruction.Input<Object> object, Instruction.Input<?>... arguments) {
            return visitor -> {
                object.write(visitor);
                for (Instruction.Input<?> argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), false);
                if (returnType == Type.VOID) visitor.visitInsn(Opcodes.ACONST_NULL);
            };
        }
    }

}
