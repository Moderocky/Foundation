package org.valross.foundation.assembler.tool;

import org.valross.foundation.Loader;
import org.valross.foundation.assembler.ClassFile;
import org.valross.foundation.assembler.attribute.ConstantValue;
import org.valross.foundation.assembler.attribute.Synthetic;
import org.valross.foundation.detail.Signature;
import org.valross.foundation.detail.Type;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.STRING;
import static org.valross.foundation.assembler.tool.Access.*;
import static org.valross.foundation.detail.Version.JAVA_21;
import static org.valross.foundation.detail.Version.RELEASE;

public class FieldBuilderTest extends ClassFileBuilderTest {

    @Override
    protected ModifiableBuilder example() {
        return new ClassFileBuilder(JAVA_21, RELEASE).field();
    }

    @Test
    public void withField() {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");
        final ClassFileBuilder builder =
            new ClassFileBuilder(JAVA_21, type).field().named("test").ofType(Object.class).exit();
        final ClassFile file = builder.build();
        final Class<?> done = this.load(loader, file, type);
        assert done != null;
        assert Type.of(done).equals(type);
        assert done.getDeclaredFields().length == 1;
        assert done.getDeclaredFields()[0].getName().equals("test");
        assert done.getDeclaredFields()[0].getType() == Object.class;
    }

    @Test
    public void deprecatedSyntheticField() {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");
        final ClassFileBuilder builder =
            new ClassFileBuilder(JAVA_21, type).field().named("test").ofType(Object.class).deprecated()
                                               .synthetic().exit();
        final ClassFile file = builder.build();
        final Class<?> done = this.load(loader, file, type);
        assert done != null;
        assert Type.of(done).equals(type);
        assert done.getDeclaredFields().length == 1;
        assert done.getDeclaredFields()[0].getName().equals("test");
        assert done.getDeclaredFields()[0].getType() == Object.class;
    }

    @Test
    public void withTwoFields() throws IllegalAccessException, NoSuchFieldException {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");
        final ClassFileBuilder builder =
            new ClassFileBuilder(JAVA_21, type).field().named("test").ofType(Object.class).exit();
        final FieldBuilder field = builder.field().setModifiers(PUBLIC, STATIC, FINAL);
        var _ = field.ofType(String.class).named("foo").constantValue(STRING, "hello");
        final ClassFile file = builder.build();
        final Class<?> done = this.load(loader, file, type);
        assert done != null;
        assert Type.of(done).equals(type);
        assert done.getDeclaredFields().length == 2;
        final Field test = done.getDeclaredField("test");
        assert test.getType() == Object.class;
        final Field foo = done.getDeclaredField("foo");
        assert foo.getType() == String.class;
        foo.setAccessible(true);
        assert foo.get(null).equals("hello");
    }

    @Test
    public void signature() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final FieldBuilder field = builder.field().signature(new Signature("test", String.class));
        assert field.name != null;
        assert field.name.ensure().is("test");
        assert field.descriptor != null;
        assert field.descriptor.ensure().is(String.class.descriptorString());
    }

    @Test
    public void named() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final FieldBuilder field = builder.field().named("blob");
        assert field.name != null;
        assert field.name.ensure().is("blob");
        assert field.descriptor == null;
    }

    @Test
    public void ofType() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final FieldBuilder field = builder.field().ofType(Type.STRING);
        assert field.name == null;
        assert field.descriptor != null;
        assert field.descriptor.ensure().is(Type.STRING.descriptorString());
    }

    @Test
    public void ofTypeCls() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final FieldBuilder field = builder.field().ofType(String.class);
        assert field.name == null;
        assert field.descriptor != null;
        assert field.descriptor.ensure().is(String.class.descriptorString());
    }

    @Test
    public void constantValue() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final FieldBuilder field = builder.field().signature(new Signature("test", String.class));
        field.constantValue(STRING, "boo");
        assert field.attributes.size() == 1;
        assert field.attributes.getFirst().build() instanceof ConstantValue value && value.attribute_name_index()
                                                                                          .ensure()
                                                                                          .is("ConstantValue") && value.constantvalue_index()
                                                                                                                       .ensure()
                                                                                                                       .is("boo");
    }

    @Test
    @Override
    public void attribute() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final FieldBuilder field = builder.field().signature(new Signature("test", String.class));
        field.constantValue(STRING, "boo");
        assert field.attributes.size() == 1;
        field.attribute(Synthetic::new);
        assert field.attributes.size() == 2;
        assert field.attributes.getFirst().build() instanceof ConstantValue value && value.attribute_name_index()
                                                                                          .ensure()
                                                                                          .is("ConstantValue") && value.constantvalue_index()
                                                                                                                       .ensure()
                                                                                                                       .is("boo");
        assert field.attributes.getLast().build() instanceof Synthetic value && value.attribute_name_index().ensure()
                                                                                     .is("Synthetic");
    }

    @Test
    public void exit() {
        assert this.example() instanceof FieldBuilder builder && builder.exit() != null;
    }

    @Test(expected = NullPointerException.class)
    public void constant() {
        assert this.example() instanceof FieldBuilder builder && builder.constant() != null;
    }

    @Test(expected = NullPointerException.class)
    public void finalise() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final FieldBuilder field = builder.field();
        field.constantValue(STRING, "boo");
        field.finalise();
    }

}