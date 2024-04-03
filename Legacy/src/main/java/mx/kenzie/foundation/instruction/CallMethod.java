package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import org.valross.foundation.assembler.code.AbstractInvokeCode;
import org.valross.foundation.assembler.constant.MethodTypeReference;
import org.valross.foundation.detail.*;

import java.lang.constant.Constable;
import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.valross.foundation.assembler.code.OpCode.*;

public class CallMethod {

    CallMethod() {
    }

    VirtualStub virtual(Member.Invocation bootstrap, Signature appearance, Constable... arguments) {
        return new VirtualStub(builder -> builder.write(INVOKEDYNAMIC.method(appearance, bootstrap, arguments)));
    }

    VirtualStub virtual(Member.Invocation bootstrap, String name, TypeDescriptor descriptor, Constable... arguments) {
        return new VirtualStub(builder -> builder.write(INVOKEDYNAMIC.method(new Signature(name, descriptor),
                                                                             bootstrap, arguments)));
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    VirtualStub metafactory(PreMethod method, Klass functionType, String name, Klass... parameters) {
        final ConstantStub stub = this.of(Object.class, functionType, name, parameters);
        return this.metafactory(stub, method.getModifiers(), method.isInterface(), method.getOwner(),
                                method.returnType(), method.name(), method.getParameters());
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    VirtualStub metafactory(Method method, Klass functionType, String name, Klass... parameters) {
        final boolean isInterface = method.getDeclaringClass().isInterface();
        final ConstantStub stub = this.of(Object.class, functionType, name, parameters);
        return this.metafactory(stub, method.getModifiers(), isInterface, Type.of(method.getDeclaringClass()),
                                Type.of(method.getReturnType()), method.getName(),
                                Type.array(method.getParameterTypes()));
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    VirtualStub metafactory(Erasure dummy, int modifiers, boolean isInterface, Klass owner, Klass result,
                            String name, Klass... parameters) {
        final Member.Invocation maker = this.handle(Modifier.PUBLIC | Modifier.STATIC, false, LambdaMetafactory.class,
                                                    CallSite.class, "metafactory", MethodHandles.Lookup.class,
                                                    String.class,
                                                    MethodType.class,
                                                    MethodType.class, MethodHandle.class, MethodType.class);
        final Member.Invocation lambda = this.handle(modifiers, isInterface, owner, result, name, parameters);
        final Descriptor descriptor = Descriptor.of(result, parameters);
        return new VirtualStub(builder -> builder.write(INVOKEDYNAMIC.method(dummy.getSignature(), maker, descriptor,
                                                                             lambda, descriptor)));
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Member.Invocation handle(int modifiers, boolean isInterface, Klass owner, Klass result, String name,
                             Klass... parameters) {
        final int code;
        if (Modifier.isStatic(modifiers)) code = MethodTypeReference.INVOKE_STATIC;
        else if (Modifier.isPrivate(modifiers)) code = MethodTypeReference.INVOKE_SPECIAL;
        else if (isInterface) code = MethodTypeReference.INVOKE_INTERFACE;
        else code = MethodTypeReference.INVOKE_VIRTUAL;
        return new Member(owner, result, name, parameters).dynamicInvocation(code, isInterface);
    }

    public Member.Invocation handle(Method method) {
        final boolean isInterface = method.getDeclaringClass().isInterface();
        return this.handle(method.getModifiers(), isInterface, Type.of(method.getDeclaringClass()),
                           Type.of(method.getReturnType()), method.getName(), Type.array(method.getParameterTypes()));
    }

    public Member.Invocation handle(PreMethod method) {
        return this.handle(method.getModifiers(), method.isInterface(), method.getOwner(), method.returnType(),
                           method.name(), method.getParameters());
    }

    public Stub of(PreClass owner, PreMethod method) {
        return new ConstantStub(owner.isInterface(), Type.of(owner), Type.of(method.returnType()), method.name(),
                                Type.array(method.getParameters()));
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
        return new ConstantStub(isInterface, Type.of(owner), Type.of(returnType), name, Type.array(parameters));
    }

    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(boolean isInterface, Klass owner, Erasure erasure) {
        return new ConstantStub(isInterface, Type.of(owner), erasure.returnType(), erasure.name(),
                                erasure.parameters());
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    ConstantStub of(Klass owner, Klass returnType, String name, Klass... parameters) {
        final boolean isInterface = owner instanceof Class<?> thing && thing.isInterface();
        return new ConstantStub(isInterface, Type.of(owner), Type.of(returnType), name, Type.array(parameters));
    }

    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    ConstantStub of(Klass owner, Erasure erasure) {
        final boolean isInterface = owner instanceof Class<?> thing && thing.isInterface();
        return new ConstantStub(isInterface, Type.of(owner), erasure.returnType(), erasure.name(),
                                erasure.parameters());
    }

    public interface Stub extends Erasure {

        default Instruction.Base callStatic(Instruction.Input<?>... arguments) {
            return builder -> {
                for (Instruction.Input argument : arguments) argument.write(builder);
                builder.write(INVOKESTATIC.method(owner(), returnType(), name(), parameters()));
                if (returnType() != Type.VOID) builder.write(POP);
            };
        }

        default <Result> Instruction.Input<Result> getStatic(Instruction.Input<?>... arguments) {
            return builder -> {
                for (Instruction.Input argument : arguments) argument.write(builder);
                builder.write(INVOKESTATIC.method(owner(), returnType(), name(), parameters()));
                if (returnType() == Type.VOID) builder.write(ACONST_NULL);
            };
        }

        default Instruction.Base call(Instruction.Input<?> object, Instruction.Input<?>... arguments) {
            final boolean face = isInterface();
            final AbstractInvokeCode code = face ? INVOKEINTERFACE : INVOKEVIRTUAL;
            return builder -> {
                object.write(builder);
                for (Instruction.Input<?> argument : arguments) argument.write(builder);
                if (face) builder.write(code.interfaceMethod(owner(), returnType(), name(), parameters()));
                else builder.write(code.method(owner(), returnType(), name(), parameters()));
                if (returnType() != Type.VOID) builder.write(POP);
            };
        }

        default <Result> Instruction.Input<Result> get(Instruction.Input<?> object, Instruction.Input<?>... arguments) {
            final AbstractInvokeCode code = isInterface() ? INVOKEINTERFACE : INVOKEVIRTUAL;
            return builder -> {
                object.write(builder);
                for (Instruction.Input<?> argument : arguments) argument.write(builder);
                builder.write(code.method(isInterface(), owner(), returnType(), name(), parameters()));
                if (returnType() == Type.VOID) builder.write(ACONST_NULL);
            };
        }

        boolean isInterface();

        Type owner();

        Type returnType();

        String name();

        Type[] parameters();

        default Signature asSignature() {
            return new Signature(name(), this);
        }

        default Member asMember() {
            return new Member(owner(), asSignature());
        }

    }

    public record VirtualStub(Instruction.Input<Object> factory) {

        @SafeVarargs
        public final Instruction.Input<Object> invoke(Instruction.Input<Object>... arguments) {
            return builder -> {
                for (Instruction.Input<Object> argument : arguments) argument.write(builder);
                this.factory.write(builder);
            };
        }

    }

    public record ConstantStub(boolean isInterface, Type owner, Type returnType, String name,
                               Type... parameters) implements Stub {

        public ConstantStub(Type owner, Type returnType, String name, Type... parameters) {
            this(owner.isKnownInterface(), owner, returnType, name, parameters);
        }

    }

}
