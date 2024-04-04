package org.valross.foundation.assembler.tool;

import org.jetbrains.annotations.Contract;
import org.valross.constantine.Constantive;
import org.valross.foundation.assembler.MethodInfo;
import org.valross.foundation.assembler.attribute.AttributeInfo;
import org.valross.foundation.assembler.code.CodeVector;
import org.valross.foundation.assembler.error.ClassBuilderException;
import org.valross.foundation.detail.Descriptor;
import org.valross.foundation.detail.Erasure;
import org.valross.foundation.detail.Signature;
import org.valross.foundation.detail.Type;

import java.lang.invoke.TypeDescriptor;
import java.util.function.Function;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public class MethodBuilder extends ModifiableBuilder implements Constantive, Erasure, Purgative {

    private static final int HAS_NAME = 0x0001, HAS_RETURN = 0x0010, HAS_PARAMETERS = 0x0100;
    protected final ClassFileBuilder.Storage storage;
    protected PoolReference name, descriptor;
    private Type returnType = Type.VOID;
    private Type[] parameters = new Type[0];
    private String rawName;
    private int details;
    private CodeBuilder code;

    public MethodBuilder(ClassFileBuilder.Storage storage) {
        this.storage = storage;
    }

    public MethodBuilder signature(Signature signature) {
        this.details |= HAS_NAME | HAS_PARAMETERS | HAS_RETURN;
        this.returnType = Type.fromDescriptor(signature);
        this.details |= HAS_RETURN;
        this.parameters = Type.parameters(signature);
        this.details |= HAS_PARAMETERS;
        this.descriptor = storage.constant(UTF8, signature.descriptorString());
        return this.named(signature.name());
    }

    public MethodBuilder erasure(Erasure erasure) {
        this.details |= HAS_NAME | HAS_PARAMETERS | HAS_RETURN;
        return this.named(erasure.name()).type(erasure.returnType(), erasure.parameters());
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor> MethodBuilder type(Klass returnType,
                                                                                            Klass... parameters) {
        this.returnType = Type.of(returnType);
        this.parameters = Type.array(parameters);
        return this.descriptor(Descriptor.of(this.returnType, this.parameters));
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> MethodBuilder returns(Klass returnType) {
        this.returnType = Type.of(returnType);
        this.details |= HAS_RETURN;
        if ((details & HAS_PARAMETERS) != 0) return this.descriptor(Descriptor.of(this.returnType, this.parameters));
        return this;
    }

    public MethodBuilder named(String name) {
        this.name = storage.constant(UTF8, rawName = name);
        this.details |= HAS_NAME;
        return this;
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor> MethodBuilder parameters(Klass... parameters) {
        this.parameters = Type.array(parameters);
        this.details |= HAS_PARAMETERS;
        if ((details & HAS_RETURN) != 0) return this.descriptor(Descriptor.of(this.returnType, this.parameters));
        return this;
    }

    private MethodBuilder descriptor(TypeDescriptor type) {
        this.descriptor = storage.constant(UTF8, type.descriptorString());
        return this;
    }

    @Override
    public MethodInfo constant() {
        this.finalise();
        return new MethodInfo(access_flags, name, descriptor, attributes());
    }

    public MethodBuilder setModifiers(Access.Method... flags) {
        return (MethodBuilder) super.setModifiers(flags);
    }

    public MethodBuilder addModifiers(Access.Method... flags) {
        return (MethodBuilder) super.addModifiers(flags);
    }

    @Override
    public MethodBuilder synthetic() {
        return (MethodBuilder) super.synthetic();
    }

    @Override
    public MethodBuilder deprecated() {
        return (MethodBuilder) super.deprecated();
    }

    @Override
    protected MethodBuilder attribute(AttributeBuilder attribute) {
        if (attribute instanceof CodeBuilder theirs) { // we can't have two codes
            if (attributes.contains(attribute)) return this;
            for (AttributeBuilder builder : attributes) {
                if (builder instanceof CodeBuilder)
                    throw new IllegalArgumentException("Method already has code attribute");
            }
            super.attribute(this.code = theirs);
            return this;
        }
        return (MethodBuilder) super.attribute(attribute);
    }

    @Override
    public ClassFileBuilder.Storage helper() {
        return storage;
    }

    public MethodBuilder attribute(Function<ClassFileBuilder.Storage, AttributeInfo.FieldAttribute> attribute) {
        return (MethodBuilder) super.makeAttribute(attribute);
    }

    @Contract(pure = true)
    public ClassFileBuilder exit() {
        return storage.source();
    }

    @Override
    public Type returnType() {
        return returnType;
    }

    @Override
    public String name() {
        return rawName;
    }

    @Override
    public Type[] parameters() {
        return parameters;
    }

    @Override
    public void finalise() {
        if (descriptor == null) {
            if (returnType != null && parameters != null)
                this.descriptor(Descriptor.of(this.returnType, this.parameters));
            else throw new ClassBuilderException("Method descriptor is missing (needs return type + parameters)");
        }
        if (name == null) throw new ClassBuilderException("Method name is missing");
        for (AttributeBuilder attribute : attributes) attribute.finalise();
    }

    public CodeBuilder code(boolean ignoreStack, boolean ignoreFrames) {
        return this.code().setTrackStack(!ignoreStack).setTrackFrames(!ignoreFrames);
    }

    public CodeBuilder code() {
        if (code != null) return code;
        this.attribute(code = new CodeBuilder(this).writingTo(new CodeVector()));
        return code;
    }

    @Override
    public void purge(ClassFileBuilder.Storage storage) {
        // todo
    }

}
