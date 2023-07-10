package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.Type;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Method;

public class CallMethod {

    CallMethod() {
    }

    public Stub of(PreClass owner, PreMethod method) {
        return new Stub(!owner.isInterface(), Type.of(owner), Type.of(method.returnType()), method.name(), Type.array(method.getParameters()));
    }

    public Stub of(Class<?> owner, String name, Class<?>... parameters) {
        try {
            final Method method = owner.getMethod(name, parameters);
            return of(owner.isInterface(), owner, method.getReturnType(), name, parameters);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(boolean isInterface, Klass owner, Klass returnType, String name, Klass... parameters) {
        return new Stub(!isInterface, Type.of(owner), Type.of(returnType), name, Type.array(parameters));
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass owner, Klass returnType, String name, Klass... parameters) {
        return new Stub(Type.of(owner), Type.of(returnType), name, Type.array(parameters));
    }

    public record Stub(boolean real, Type owner, Type returnType, String name, Type... parameters) {
        public Stub(Type owner, Type returnType, String name, Type... parameters) {
            this(true, owner, returnType, name, parameters);
        }

        public Instruction.Base callStatic(Instruction.Input<?>... arguments) {
            return visitor -> {
                for (Instruction.Input argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), false);
                if (returnType != Type.VOID) visitor.visitInsn(Opcodes.POP);
            };
        }

        public <Result> Instruction.Input<Result> getStatic(Instruction.Input<?>... arguments) {
            return visitor -> {
                for (Instruction.Input argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), false);
                if (returnType == Type.VOID) visitor.visitInsn(Opcodes.ACONST_NULL);
            };
        }

        public Instruction.Base call(Instruction.Input<Object> object, Instruction.Input<?>... arguments) {
            final int instruction = real ? Opcodes.INVOKEVIRTUAL : Opcodes.INVOKEINTERFACE;
            return visitor -> {
                object.write(visitor);
                for (Instruction.Input<?> argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(instruction, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), !real);
                if (returnType != Type.VOID) visitor.visitInsn(Opcodes.POP);
            };
        }

        public <Result> Instruction.Input<Result> get(Instruction.Input<Object> object, Instruction.Input<?>... arguments) {
            final int instruction = real ? Opcodes.INVOKEVIRTUAL : Opcodes.INVOKEINTERFACE;
            return visitor -> {
                object.write(visitor);
                for (Instruction.Input<?> argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(instruction, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), !real);
                if (returnType == Type.VOID) visitor.visitInsn(Opcodes.ACONST_NULL);
            };
        }
    }

}
