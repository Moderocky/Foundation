package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.Loader;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.assembler.ClassFile;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static mx.kenzie.foundation.assembler.code.OpCode.ARETURN;
import static mx.kenzie.foundation.assembler.code.OpCode.LDC;
import static mx.kenzie.foundation.assembler.tool.Access.*;
import static mx.kenzie.foundation.assembler.tool.Version.JAVA_21;
import static mx.kenzie.foundation.assembler.tool.Version.RELEASE;

public class ClassFileBuilderTest extends ModifiableBuilderTest {

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
    public void withMethod() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, type);
        final MethodBuilder method = builder.method();
        method.returns(String.class).named("blob").setModifiers(PUBLIC, STATIC);
        final CodeBuilder code = method.code();
        code.write(LDC.value("hello"), ARETURN);
        final ClassFile file = builder.build();
        final Class<?> done = this.load(loader, file, type);
        assert done != null;
        assert Type.of(done).equals(type);
        assert done.getDeclaredFields().length == 0;
        final Method found = done.getDeclaredMethod("blob");
        found.setAccessible(true);
        assert found.invoke(null).equals("hello");
    }

    @Override
    protected ModifiableBuilder example() {
        return new ClassFileBuilder(JAVA_21, RELEASE);
    }

}