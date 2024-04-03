package org.valross.foundation.assembler.tool;

import org.jetbrains.annotations.Contract;
import org.valross.constantine.Constantive;
import org.valross.foundation.assembler.FieldInfo;
import org.valross.foundation.assembler.attribute.AttributeInfo;
import org.valross.foundation.assembler.attribute.ConstantValue;
import org.valross.foundation.assembler.constant.ConstantPoolInfo;
import org.valross.foundation.assembler.constant.ConstantType;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.detail.Erasure;
import org.valross.foundation.detail.Signature;
import org.valross.foundation.detail.Type;

import java.lang.constant.Constable;
import java.lang.invoke.TypeDescriptor;
import java.util.Objects;
import java.util.function.Function;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public class FieldBuilder extends ModifiableBuilder implements Constantive, Erasure {

    protected final ClassFileBuilder.Storage storage;
    protected PoolReference name, descriptor;

    public FieldBuilder(ClassFileBuilder.Storage storage) {
        this.storage = storage;
    }

    public FieldBuilder signature(Signature signature) {
        return this.named(signature.name()).ofType(signature.descriptor());
    }

    public FieldBuilder named(String name) {
        this.name = storage.constant(UTF8, name);
        return this;
    }

    public <Klass extends TypeDescriptor> FieldBuilder ofType(Klass type) {
        this.descriptor = storage.constant(UTF8, type.descriptorString());
        return this;
    }

    public FieldBuilder constantValue(Constable value) {
        return this.attribute(new ConstantValue(storage.constant(UTF8, ConstantValue.ATTRIBUTE_NAME),
                                                this.storage.constant(value)));
    }

    public <Value extends Constable> FieldBuilder constantValue(ConstantType<?, Value> type, Value value) {
        return this.attribute(new ConstantValue(storage.constant(UTF8, ConstantValue.ATTRIBUTE_NAME),
                                                this.storage.constant(type, value)));
    }

    public FieldBuilder attribute(AttributeInfo.FieldAttribute attribute) {
        return (FieldBuilder) super.attribute(attribute);
    }

    public FieldBuilder attribute(Function<ClassFileBuilder.Storage, AttributeInfo.FieldAttribute> attribute) {
        return (FieldBuilder) super.makeAttribute(attribute);
    }

    @Override
    public FieldBuilder synthetic() {
        return (FieldBuilder) super.synthetic();
    }

    @Override
    public FieldBuilder deprecated() {
        return (FieldBuilder) super.deprecated();
    }

    @Override
    public ClassFileBuilder.Storage helper() {
        return storage;
    }

    @Contract(pure = true)
    public ClassFileBuilder exit() {
        return storage.source();
    }

    @Override
    @Contract(pure = true)
    public FieldInfo constant() {
        this.finalise();
        return new FieldInfo(access_flags, name, descriptor, U2.valueOf(attributes.size()),
                             attributes.toArray(new AttributeInfo[0]));
    }

    @Override
    public Type returnType() {
        return ConstantPoolInfo.NAME_AND_TYPE.unpack(descriptor.get()).returnType();
    }

    @Override
    public String name() {
        return ConstantPoolInfo.NAME_AND_TYPE.unpack(descriptor.get()).name();
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

    public FieldBuilder setModifiers(Access.Field... flags) {
        return (FieldBuilder) super.setModifiers(flags);
    }

    public FieldBuilder addModifiers(Access.Field... flags) {
        return (FieldBuilder) super.addModifiers(flags);
    }

    @Override
    public void finalise() {
        Objects.requireNonNull(name, "No field name provided");
        Objects.requireNonNull(descriptor, "No field type (descriptor) provided");
    }

}
