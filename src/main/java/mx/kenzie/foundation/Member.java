package mx.kenzie.foundation;

import mx.kenzie.foundation.instruction.AccessField;
import mx.kenzie.foundation.instruction.CallMethod;
import org.valross.constantine.RecordConstant;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Field;

/**
 * A 'member' of a class, such as a method or a field.
 * This contains all the information needed to reference the member directly.
 * It is not specified what type of thing this is (e.g. a method or a field)
 * although that can be inferred from its signature where necessary.
 *
 * @param owner     The class the member comes from
 * @param signature The member's signature
 */
public record Member(Type owner, Signature signature) implements Descriptor, RecordConstant {

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> Member(Klass owner, Signature signature) {
        this(Type.of(owner), signature);
    }

    @SafeVarargs
    public <Klass extends java.lang.reflect.Type & TypeDescriptor> Member(Klass owner, Klass returnType, String name, Klass... parameters) {
        this(Type.of(owner), new Signature(returnType, name, parameters));
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> Member(Klass owner, String name, Klass type) {
        this(Type.of(owner), new Signature(name, type));
    }

    public Member(Field field) {
        this(field.getDeclaringClass(), field.getName(), field.getType());
    }

    public Member(java.lang.reflect.Method method) {
        this(method.getDeclaringClass(), new Signature(method));
    }

    public Member(AccessField.Stub field) {
        this(field.owner(), new Signature(field));
    }

    public Member(CallMethod.Stub method) {
        this(method.owner(), new Signature(method));
    }

    @Override
    public String descriptorString() {
        return signature.descriptorString();
    }

}
