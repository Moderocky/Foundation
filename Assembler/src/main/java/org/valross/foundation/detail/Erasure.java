package org.valross.foundation.detail;

import org.valross.constantine.Constant;
import org.valross.constantine.RecordConstant;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Executable;
import java.util.Objects;

/**
 * Something representing the 'erasure' (form) of a class member, typically a method.
 * In practical terms the erasure is everything needed to access or 'invoke' the member,
 * when provided with its declaring class.
 * For a method, this would be its return type, name and parameters.
 * For a field, this would be its type and name.
 * In the case of a field, its type is considered to be the 'return type' (the result you get from accessing it)
 * and the parameters are left empty. Note that if using a dynamic field accessor (method handle) the field type
 * would be its first parameter.
 */
public interface Erasure extends Descriptor, TypeDescriptor {

    static Erasure of(Erasure erasure) {
        return erasure;
    }

    static Erasure of(Executable executable) {
        if (executable instanceof java.lang.reflect.Method method)
            return new Simple(method.getReturnType(), method.getName(), method.getParameterTypes());
        final Type returnType = Type.of(executable.getAnnotatedReturnType().getType());
        final String name = executable.getName();
        final Type[] parameters = Type.array(executable.getParameters());
        return new Simple(returnType, name, parameters);
    }

    @SafeVarargs
    static <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Erasure of(Klass returnType, String name, Klass... parameters) {
        return new Simple(returnType, name, parameters);
    }

    default Constant constant() {
        return new Signature(this);
    }

    Type returnType();

    String name();

    Type[] parameters();

    default String descriptorString() {
        return Type.methodDescriptor(this.returnType(), this.parameters());
    }

    default boolean isMethod() {
        return this.descriptorString().startsWith("(");
    }

    default boolean isField() {
        return !this.isMethod();
    }

    default Signature asFieldErasure() {
        return new Signature(this.name(), this.returnType());
    }

    default Erasure asMethodErasure() {
        return new Simple(this.returnType(), this.name(), this.parameters());
    }

    default Signature getSignature() {
        if (this instanceof Signature signature) return signature;
        return new Signature(this);
    }

    default boolean overloads(Erasure other) {
        return Objects.equals(this.name(), other.name()) && Objects.equals(this.returnType(), other.returnType());
    }

    record Simple(Type returnType, String name, Type... parameters) implements Erasure, RecordConstant {

        @SafeVarargs
        public <Klass extends java.lang.reflect.Type & TypeDescriptor> Simple(Klass returnType, CharSequence name,
                                                                              Klass... parameters) {
            this(Type.of(returnType), name.toString(), Type.array(parameters));
        }

        public Simple(java.lang.reflect.Method method) {
            this(method.getReturnType(), method.getName(), method.getParameterTypes());
        }

        @Override
        public Simple constant() {
            return this;
        }

    }

}
