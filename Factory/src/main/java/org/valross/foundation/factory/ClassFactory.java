package org.valross.foundation.factory;

import org.intellij.lang.annotations.MagicConstant;
import org.valross.foundation.assembler.ClassFile;
import org.valross.foundation.assembler.tool.Access;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.detail.Signature;
import org.valross.foundation.detail.Type;
import org.valross.foundation.detail.TypeHint;
import org.valross.foundation.detail.Version;

import java.lang.invoke.TypeDescriptor;

/**
 * A factory for building a compiled, loadable Java class.
 * This wraps the class file assembler, but mimics Java's paradigm for a more
 * familiar code format.
 */
public class ClassFactory extends Factory<ClassFileBuilder> implements TypeHint {

    protected ClassFactory(ClassFileBuilder builder) {
        super(builder);
    }

    public static ClassFactory create() {
        return create(Version.JAVA_22, false);
    }

    public static ClassFactory create(@MagicConstant(valuesFromClass = Version.class) int version, boolean preview) {
        return new ClassFactory(new ClassFileBuilder(version, preview ? Version.PREVIEW : Version.RELEASE));
    }

    public ClassFactory modifiers(Access.Type... modifiers) {
        this.builder.setModifiers(modifiers);
        return this;
    }

    public ClassFactory type(TypeDescriptor type) {
        this.builder.setType(Type.fromDescriptor(type));
        return this;
    }

    public ClassFactory type(String packageName, String className) {
        return this.type(Type.of(packageName, className));
    }

    public ClassFactory extend(TypeDescriptor type) {
        this.builder.setSuperType(Type.fromDescriptor(type));
        return this;
    }

    public ClassFactory implement(TypeDescriptor... types) {
        this.builder.addInterfaces(Type.array(types));
        return this;
    }

    public FieldFactory field(String name, TypeDescriptor type) {
        return new FieldFactory(this.builder.field().named(name).ofType(Type.fromDescriptor(type)));
    }

    public FieldFactory field(Signature signature) {
        return new FieldFactory(this.builder.field().signature(signature));
    }

    @Override
    public String descriptorString() {
        return builder.descriptorString();
    }

    @Override
    public String getTypeName() {
        return builder.getTypeName();
    }

    @Override
    public ClassFile constant() {
        return builder.constant();
    }

}
