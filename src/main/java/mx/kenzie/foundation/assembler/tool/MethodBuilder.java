package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.Descriptor;
import mx.kenzie.foundation.MethodErasure;
import mx.kenzie.foundation.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.assembler.MethodInfo;
import mx.kenzie.foundation.assembler.attribute.AttributeInfo;
import org.jetbrains.annotations.Contract;
import org.valross.constantine.Constantive;

import java.lang.invoke.TypeDescriptor;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public class MethodBuilder extends ModifiableBuilder implements Constantive, MethodErasure {

    private static final int HAS_NAME = 0x0001, HAS_RETURN = 0x0010, HAS_PARAMETERS = 0x0100;
    protected final ClassFileBuilder.Storage storage;
    protected PoolReference name, descriptor;
    private Type returnType = Type.VOID;
    private Type[] parameters = new Type[0];
    private int details;

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

    public MethodBuilder erasure(MethodErasure erasure) {
        this.details |= HAS_NAME | HAS_PARAMETERS | HAS_RETURN;
        return this.named(erasure.name()).type(erasure.returnType(), erasure.parameters());
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> MethodBuilder type(Klass returnType,
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
        this.name = storage.constant(UTF8, name);
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
        return null;
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
    protected MethodBuilder attribute(AttributeInfo attribute) {
        return (MethodBuilder) super.attribute(attribute);
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
    public Type returnType() {
        return returnType;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public Type[] parameters() {
        return new Type[0];
    }

}
