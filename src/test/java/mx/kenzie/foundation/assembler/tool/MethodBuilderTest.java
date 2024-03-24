package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.Loader;
import mx.kenzie.foundation.detail.MethodErasure;
import mx.kenzie.foundation.detail.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.assembler.ClassFile;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static mx.kenzie.foundation.assembler.code.OpCode.ARETURN;
import static mx.kenzie.foundation.assembler.code.OpCode.LDC;
import static mx.kenzie.foundation.assembler.tool.Access.PUBLIC;
import static mx.kenzie.foundation.assembler.tool.Access.STATIC;
import static mx.kenzie.foundation.detail.Version.JAVA_21;
import static mx.kenzie.foundation.detail.Version.RELEASE;

public class MethodBuilderTest extends ClassFileBuilderTest {

    @Override
    protected MethodBuilder example() {
        return new ClassFileBuilder(JAVA_21, RELEASE).method();
    }

    @Test
    public void withMethod() throws Throwable {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, type);
        final MethodBuilder method = builder.method();
        method.returns(String.class).named("blob").setModifiers(PUBLIC, STATIC);
        final CodeBuilder code = method.code();
        code.write(LDC.value("hello"), ARETURN);
        method.deprecated();
        final ClassFile file = builder.build();
        final Class<?> done = this.load(loader, file, type);
        assert done != null;
        assert Type.of(done).equals(type);
        assert done.getDeclaredFields().length == 0;
        final Method found = done.getDeclaredMethod("blob");
        found.setAccessible(true);
        assert found.invoke(null).equals("hello");
    }

    @Test
    public void signature() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final MethodBuilder method = builder.method().signature(new Signature(boolean[].class, "test", String.class,
            int.class));
        assert method.name != null;
        assert method.name.ensure().is("test");
        assert method.descriptor != null;
        assert method.descriptor.ensure().is(Type.methodDescriptor(boolean[].class, String.class, int.class));
    }

    @Test
    public void named() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final MethodBuilder method = builder.method().named("blob");
        assert method.name != null;
        assert method.name.ensure().is("blob");
        assert method.descriptor == null;
    }

    @Test
    public void erasure() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final MethodBuilder method = builder.method().erasure(MethodErasure.of(boolean[].class, "test", String.class,
            int.class));
        assert method.name != null;
        assert method.name.ensure().is("test");
        assert method.descriptor != null;
        assert method.descriptor.ensure().is(Type.methodDescriptor(boolean[].class, String.class, int.class));
    }

    @Test
    public void type() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final MethodBuilder method = builder.method().type(void.class);
        assert method.name == null;
        assert method.descriptor != null;
        assert method.descriptor.ensure().is(Type.methodDescriptor(void.class));
    }

    @Test
    public void returns() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final MethodBuilder method = builder.method().returns(boolean[].class);
        assert method.name == null;
        assert method.descriptor == null;
        assert method.returnType().equals(Type.of(boolean[].class));
        method.named("t");
        method.finalise();
        assert method.descriptor != null;
        assert method.descriptor.ensure().is(Type.methodDescriptor(boolean[].class));
    }

    @Test
    public void parameters() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final MethodBuilder method = builder.method().parameters(String[].class,
            int.class);
        assert method.name == null;
        assert method.descriptor == null;
        assert Arrays.equals(method.parameters(), Type.array(String[].class, int.class));
        method.named("t");
        method.finalise();
        assert method.descriptor != null;
        assert method.descriptor.ensure().is(Type.methodDescriptor(void.class, String[].class, int.class));
    }

}