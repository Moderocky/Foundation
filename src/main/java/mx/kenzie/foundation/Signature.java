package mx.kenzie.foundation;

import mx.kenzie.foundation.instruction.AccessField;
import org.valross.constantine.RecordConstant;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Field;

/**
 * Represents the 'signature' of a class member (e.g. field, method).
 * This is its name and type information, but without any references to the class it originates from.
 *
 * @param name       the member name (e.g. field name, method name)
 * @param descriptor the member descriptor (field = type descriptor, method = method descriptor)
 */
public record Signature(String name, Descriptor descriptor) implements RecordConstant, Descriptor {

    @SafeVarargs
    public <Klass extends java.lang.reflect.Type & TypeDescriptor> Signature(Klass returnType, String name,
                                                                             Klass... parameters) {
        this(name, Descriptor.of(returnType, parameters));
    }

    public Signature(Field field) {
        this(field.getName(), Type.of(field.getType()));
    }

    public Signature(java.lang.reflect.Method method) {
        this(method.getReturnType(), method.getName(), method.getParameterTypes());
    }

    public Signature(AccessField.Stub field) {
        this(field.name(), field.type());
    }

    public Signature(MethodErasure method) {
        this(method.returnType(), method.name(), method.parameters());
    }

    public Signature(String name, TypeDescriptor descriptor) {
        this(name, descriptor instanceof Descriptor ours ? ours : Descriptor.of(descriptor.descriptorString()));
    }

    @Override
    public String descriptorString() {
        return descriptor.descriptorString();
    }

}
