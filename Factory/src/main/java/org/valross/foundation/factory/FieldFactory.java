package org.valross.foundation.factory;

import org.valross.foundation.assembler.tool.Access;
import org.valross.foundation.assembler.tool.FieldBuilder;
import org.valross.foundation.detail.Erasure;
import org.valross.foundation.detail.Signature;
import org.valross.foundation.detail.Type;

import java.lang.constant.Constable;
import java.lang.invoke.TypeDescriptor;

public class FieldFactory extends ModifiableFactory<FieldBuilder> implements MemberFactory, Erasure {

    protected final ClassFactory factory;

    protected FieldFactory(FieldBuilder builder, ClassFactory factory) {
        super(builder);
        this.factory = factory;
    }

    @Override
    public FieldFactory name(String name) {
        this.builder.named(name);
        return this;
    }

    public FieldFactory type(TypeDescriptor type) {
        this.builder.ofType(Type.fromDescriptor(type));
        return this;
    }

    @Override
    public FieldFactory signature(Signature signature) {
        this.builder.signature(signature);
        return this;
    }

    public FieldFactory modifiers(Access.Field... modifiers) {
        this.builder.setModifiers(modifiers);
        return this;
    }

    public FieldFactory constant(Constable value) {
        this.builder.constantValue(value);
        return this;
    }

    @Override
    public ClassFactory source() {
        return factory;
    }

    @Override
    public Type returnType() {
        return builder.returnType();
    }

    @Override
    public String name() {
        return builder.name();
    }

    @Override
    public Type[] parameters() {
        return new Type[0];
    }

    @Override
    public boolean isField() {
        return true;
    }

    @Override
    public boolean isMethod() {
        return false;
    }

}
