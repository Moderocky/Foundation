package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.Loader;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.assembler.ClassFile;
import org.junit.Test;

import java.lang.reflect.Field;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.STRING;
import static mx.kenzie.foundation.assembler.tool.Access.*;
import static mx.kenzie.foundation.assembler.tool.Version.JAVA_21;

public class ClassFileBuilderTest {

    @Test
    public void simple() {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, type);
        builder.setModifiers(PUBLIC, FINAL, VOLATILE);
        builder.setType(type);
        final ClassFile file = builder.build();
        final Class<?> done = this.load(loader, file, type);
        assert done != null;
        assert Type.of(done).equals(type);
        assert done.getDeclaredFields().length == 0;
    }

    @Test
    public void withField() {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, type)
            .field().named("test").ofType(Object.class).exit();
        final ClassFile file = builder.build();
        final Class<?> done = this.load(loader, file, type);
        assert done != null;
        assert Type.of(done).equals(type);
        assert done.getDeclaredFields().length == 1;
        assert done.getDeclaredFields()[0].getName().equals("test");
        assert done.getDeclaredFields()[0].getType() == Object.class;
    }

    @Test
    public void withTwoFields()
        throws IllegalAccessException, NoSuchFieldException {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, type)
            .field().named("test").ofType(Object.class).exit();
        final FieldBuilder field = builder.field().setModifiers(PUBLIC, STATIC, FINAL);
        field.ofType(String.class).named("foo").constantValue(STRING,
            "hello");
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
        System.out.println(foo.get(null));
    }

    protected Class<?> load(Loader loader, ClassFile file, Type type) {
        return loader.loadClass(type.getTypeName(), file.binary());
    }

}