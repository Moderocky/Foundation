package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import mx.kenzie.foundation.Type;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class CallMethod {

    CallMethod() {
    }

    private org.objectweb.asm.Type type(Handle handle) {
        return org.objectweb.asm.Type.getType(handle.getDesc());
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    VirtualStub virtual(Handle maker, Handle lambda, Klass result, String name, Klass... parameters) {
        final String dummy = Type.methodDescriptor(result, parameters);
        return new VirtualStub(visitor -> visitor.visitInvokeDynamicInsn(name, dummy, maker, this.type(lambda), lambda, this.type(lambda)));
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    VirtualStub metafactory(PreMethod method, Klass functionType, String name, Klass... parameters) {
        final Stub stub = this.of(Object.class, functionType, name, parameters);
        return this.metafactory(stub, method.getModifiers(), method.isInterface(), method.getOwner(), method.returnType(), method.name(), method.getParameters());
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    VirtualStub metafactory(Method method, Klass functionType, String name, Klass... parameters) {
        final boolean isInterface = method.getDeclaringClass().isInterface();
        final Stub stub = this.of(Object.class, functionType, name, parameters);
        return this.metafactory(stub, method.getModifiers(), isInterface, Type.of(method.getDeclaringClass()), Type.of(method.getReturnType()), method.getName(), Type.array(method.getParameterTypes()));
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    VirtualStub metafactory(Stub dummy, int modifiers, boolean isInterface, Klass owner, Klass result, String name, Klass... parameters) {
        final Handle maker = this.handle(Modifier.PUBLIC | Modifier.STATIC, false, LambdaMetafactory.class, CallSite.class, "metafactory", MethodHandles.Lookup.class, String.class, MethodType.class, MethodType.class, MethodHandle.class, MethodType.class);
        final Handle lambda = this.handle(modifiers, isInterface, owner, result, name, parameters);
        final String descriptor = Type.methodDescriptor(result, parameters);
        return new VirtualStub(visitor -> visitor.visitInvokeDynamicInsn(dummy.name(), Type.methodDescriptor(dummy.returnType, dummy.parameters), maker, org.objectweb.asm.Type.getType(descriptor), lambda, org.objectweb.asm.Type.getType(descriptor)));
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Handle handle(int modifiers, boolean isInterface, Klass owner, Klass result, String name, Klass... parameters) {
        final int code;
        if (Modifier.isStatic(modifiers)) code = Opcodes.H_INVOKESTATIC;
        else if (Modifier.isPrivate(modifiers)) code = Opcodes.H_INVOKESPECIAL;
        else if (isInterface) code = Opcodes.H_INVOKEINTERFACE;
        else code = Opcodes.H_INVOKEVIRTUAL;
        return new Handle(code, Type.of(owner).internalName(), name, Type.methodDescriptor(result, parameters), isInterface);
    }

    public Handle handle(Method method) {
        final boolean isInterface = method.getDeclaringClass().isInterface();
        return this.handle(method.getModifiers(), isInterface, Type.of(method.getDeclaringClass()), Type.of(method.getReturnType()), method.getName(), Type.array(method.getParameterTypes()));
    }

    public Handle handle(PreMethod method) {
        return this.handle(method.getModifiers(), method.isInterface(), method.getOwner(), method.returnType(), method.name(), method.getParameters());
    }

    public Stub of(PreClass owner, PreMethod method) {
        return new Stub(owner.isInterface(), Type.of(owner), Type.of(method.returnType()), method.name(), Type.array(method.getParameters()));
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
        return new Stub(isInterface, Type.of(owner), Type.of(returnType), name, Type.array(parameters));
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass owner, Klass returnType, String name, Klass... parameters) {
        final boolean isInterface = owner instanceof Class<?> thing && thing.isInterface();
        return new Stub(isInterface, Type.of(owner), Type.of(returnType), name, Type.array(parameters));
    }

    public record VirtualStub(Instruction.Input<Object> factory) {

        @SafeVarargs
        public final Instruction.Input<Object> invoke(Instruction.Input<Object>... arguments) {
            return visitor -> {
                for (Instruction.Input<Object> argument : arguments) argument.write(visitor);
                this.factory.write(visitor);
            };
        }

    }

    public record Stub(boolean isInterface, Type owner, Type returnType, String name, Type... parameters) {
        public Stub(Type owner, Type returnType, String name, Type... parameters) {
            this(true, owner, returnType, name, parameters);
        }

        public Instruction.Base callStatic(Instruction.Input<?>... arguments) {
            return visitor -> {
                for (Instruction.Input argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), isInterface);
                if (returnType != Type.VOID) visitor.visitInsn(Opcodes.POP);
            };
        }

        public <Result> Instruction.Input<Result> getStatic(Instruction.Input<?>... arguments) {
            return visitor -> {
                for (Instruction.Input argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), isInterface);
                if (returnType == Type.VOID) visitor.visitInsn(Opcodes.ACONST_NULL);
            };
        }

        public Instruction.Base call(Instruction.Input<?> object, Instruction.Input<?>... arguments) {
            final int instruction = isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;
            return visitor -> {
                object.write(visitor);
                for (Instruction.Input<?> argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(instruction, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), isInterface);
                if (returnType != Type.VOID) visitor.visitInsn(Opcodes.POP);
            };
        }

        public <Result> Instruction.Input<Result> get(Instruction.Input<?> object, Instruction.Input<?>... arguments) {
            final int instruction = isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;
            return visitor -> {
                object.write(visitor);
                for (Instruction.Input<?> argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(instruction, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), isInterface);
                if (returnType == Type.VOID) visitor.visitInsn(Opcodes.ACONST_NULL);
            };
        }
    }

}
