package org.valross.foundation.factory;

import org.junit.Test;
import org.valross.foundation.Loader;
import org.valross.foundation.assembler.constant.ConstantPoolInfo;
import org.valross.foundation.assembler.tool.Access;
import org.valross.foundation.detail.Type;
import org.valross.foundation.detail.Version;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import static org.valross.foundation.assembler.tool.Access.*;

public class ClassFactoryTest {

    protected static final Type TYPE = Type.of("org.example", "Test");

    protected ClassFactory classFactory() {
        return ClassFactory.create(Version.JAVA_22, false).type(TYPE).modifiers(PUBLIC);
    }

    protected MethodFactory methodFactory() {
        return this.classFactory().method(Object.class, "test").modifiers(PUBLIC, STATIC);
    }

    protected void test(Object found, Object expected) {
        assert Objects.equals(found, expected) : "Expected + '" + expected + "', found '" + found + "'";
    }

    protected void test(MethodFactory factory, Object result) {
        final Class<?> type = Loader.createDefault().loadClass(factory.source());
        assert type != null;
        final Object object;
        try {
            final Method method = type.getDeclaredMethod(factory.name());
            method.setAccessible(true);
            object = method.invoke(null);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
        this.test(object, result);
    }

    @Test
    public void create() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type(TYPE);
        assert factory.constant() != null;
        assert factory.constant().minor_version().shortValue() == 0;
        assert factory.constant().major_version().shortValue() == Version.JAVA_8;
    }

    @Test
    public void modifiers() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type(TYPE);
        factory.modifiers(PUBLIC, FINAL);
        assert factory.constant() != null;
        assert Access.is(factory.constant().access_flags(), PUBLIC);
        assert Access.is(factory.constant().access_flags(), FINAL);
        assert !Access.is(factory.constant().access_flags(), STATIC);
        assert !Access.is(factory.constant().access_flags(), PRIVATE);
    }

    @Test
    public void type() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type(TYPE);
        assert factory.constant() != null;
        assert ConstantPoolInfo.TYPE.unpack(factory.constant().this_class().get())
                                    .equals(Type.of(TYPE));
    }

    @Test
    public void extend() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type(TYPE);
        factory.extend(String.class);
        assert factory.constant() != null;
        assert ConstantPoolInfo.TYPE.unpack(factory.constant().super_class().get()).equals(Type.of(String.class));
    }

    @Test
    public void implement() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type(TYPE);
        factory.implement(Runnable.class);
        assert factory.constant() != null;
        assert ConstantPoolInfo.TYPE.unpack(factory.constant().interfaces()[0].get()).equals(Type.of(Runnable.class));
    }

    @Test
    public void field() throws NoSuchFieldException, IllegalAccessException {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_22, false).type(TYPE)
                                                 .modifiers(PUBLIC, ABSTRACT, INTERFACE);
        factory.field("BLOB", String.class).modifiers(PUBLIC, STATIC, FINAL).constant("foo");
        final Class<?> loaded = Loader.createDefault().loadClass(factory);
        assert loaded.isInterface();
        final var field = loaded.getDeclaredField("BLOB");
        assert field.get(null).equals("foo");
    }

    @Test
    public void method() throws NoSuchMethodException {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_22, false).type(TYPE)
                                                 .modifiers(PUBLIC, ABSTRACT, INTERFACE);
        factory.method(void.class, "run").modifiers(PUBLIC, ABSTRACT);
        final Class<?> loaded = Loader.createDefault().loadClass(factory);
        assert loaded.isInterface();
        final var _ = loaded.getDeclaredMethod("run");
    }

    @Test
    public void descriptorString() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type(TYPE);
        assert factory.descriptorString().equals(TYPE.descriptorString());
    }

    @Test
    public void getTypeName() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_8, false).type(TYPE);
        assert factory.getTypeName().equals(TYPE.getTypeName());
    }

    @Test
    public void constant() {
        final ClassFactory factory = ClassFactory.create(Version.JAVA_22, false).type(TYPE)
                                                 .modifiers(PUBLIC, ABSTRACT, INTERFACE);
        final Class<?> loaded = Loader.createDefault().loadClass(factory);
        assert loaded != null;
        assert loaded.isInterface();
    }

}