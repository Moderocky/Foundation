package mx.kenzie.foundation.detail;

import mx.kenzie.foundation.Type;
import org.valross.constantine.RecordConstant;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Executable;
import java.util.Objects;

public interface MethodErasure extends TypeDescriptor {

    static MethodErasure of(MethodErasure erasure) {
        return erasure;
    }

    static MethodErasure of(Executable executable) {
        if (executable instanceof java.lang.reflect.Method method)
            return new Simple(method.getReturnType(), method.getName(), method.getParameterTypes());
        final Type returnType = Type.of(executable.getAnnotatedReturnType().getType());
        final String name = executable.getName();
        final Type[] parameters = Type.array(executable.getParameters());
        return new Simple(returnType, name, parameters);
    }

    @SafeVarargs
    static <Klass extends java.lang.reflect.Type & TypeDescriptor>
    MethodErasure of(Klass returnType, String name, Klass... parameters) {
        return new Simple(returnType, name, parameters);
    }

    Type returnType();

    String name();

    Type[] parameters();

    default String descriptorString() {
        return Type.methodDescriptor(this.returnType(), this.parameters());
    }

    default boolean overloads(MethodErasure other) {
        return Objects.equals(this.name(), other.name()) && Objects.equals(this.returnType(), other.returnType());
    }

    record Simple(Type returnType, String name, Type... parameters) implements MethodErasure, RecordConstant {

        @SafeVarargs
        public <Klass extends java.lang.reflect.Type & TypeDescriptor> Simple(Klass returnType, CharSequence name,
                                                                              Klass... parameters) {
            this(Type.of(returnType), name.toString(), Type.array(parameters));
        }

        public Simple(java.lang.reflect.Method method) {
            this(method.getReturnType(), method.getName(), method.getParameterTypes());
        }

    }

}
