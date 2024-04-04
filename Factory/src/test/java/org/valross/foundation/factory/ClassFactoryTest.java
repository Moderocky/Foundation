package org.valross.foundation.factory;

import org.junit.Test;
import org.valross.foundation.assembler.constant.ConstantPoolInfo;
import org.valross.foundation.assembler.tool.Access;
import org.valross.foundation.detail.Type;
import org.valross.foundation.detail.Version;

import static org.valross.foundation.assembler.tool.Access.*;

public class ClassFactoryTest {

    @Test
    public void create() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type("org.example", "Test");
        assert factory.constant() != null;
        assert factory.constant().minor_version().shortValue() == 0;
        assert factory.constant().major_version().shortValue() == Version.JAVA_8;
    }

    @Test
    public void modifiers() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type("org.example", "Test");
        factory.modifiers(PUBLIC, FINAL);
        assert factory.constant() != null;
        assert Access.is(factory.constant().access_flags(), PUBLIC);
        assert Access.is(factory.constant().access_flags(), FINAL);
        assert !Access.is(factory.constant().access_flags(), STATIC);
        assert !Access.is(factory.constant().access_flags(), PRIVATE);
    }

    @Test
    public void type() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type("org.example", "Test");
        assert factory.constant() != null;
        assert ConstantPoolInfo.TYPE.unpack(factory.constant().this_class().get())
                                    .equals(Type.of("org.example", "Test"));
    }

    @Test
    public void extend() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type("org.example", "Test");
        factory.extend(String.class);
        assert factory.constant() != null;
        assert ConstantPoolInfo.TYPE.unpack(factory.constant().super_class().get()).equals(Type.of(String.class));
    }

    @Test
    public void implement() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type("org.example", "Test");
        factory.implement(Runnable.class);
        assert factory.constant() != null;
        assert ConstantPoolInfo.TYPE.unpack(factory.constant().interfaces()[0].get()).equals(Type.of(Runnable.class));
    }

    @Test
    public void field() {
    }

    @Test
    public void method() {
    }

    @Test
    public void descriptorString() {
        final Type type = Type.of("org.example", "Test");
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type(type);
        assert factory.descriptorString().equals(type.descriptorString());
    }

    @Test
    public void getTypeName() {
        final Type type = Type.of("org.example", "Test");
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type(type);
        assert factory.getTypeName().equals(type.getTypeName());
    }

    @Test
    public void constant() {
    }

}