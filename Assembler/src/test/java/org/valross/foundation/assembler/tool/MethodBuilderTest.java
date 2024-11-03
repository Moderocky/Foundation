package org.valross.foundation.assembler.tool;

import org.valross.foundation.Loader;
import org.valross.foundation.assembler.ClassFile;
import org.valross.foundation.detail.Erasure;
import org.valross.foundation.detail.Signature;
import org.valross.foundation.detail.Type;
import org.jetbrains.annotations.Contract;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static org.valross.foundation.assembler.code.OpCode.ARETURN;
import static org.valross.foundation.assembler.code.OpCode.LDC;
import static org.valross.foundation.assembler.tool.Access.PUBLIC;
import static org.valross.foundation.assembler.tool.Access.STATIC;
import static org.valross.foundation.detail.Version.JAVA_21;
import static org.valross.foundation.detail.Version.RELEASE;

public class MethodBuilderTest extends ClassFileBuilderTest {

    @Override
    protected MethodBuilder example() {
        return new ClassFileBuilder(JAVA_21, RELEASE).method();
    }

    @Contract(pure = true)
    protected Method compileForTest(MethodBuilder builder) throws NoSuchMethodException {
        final Loader loader = Loader.createDefault();
        final ClassFile file = builder.exit().build();
        final Class<?> done;
        try {
            done = this.load(loader, file, Type.of("org.example", "Test"));
            assert done != null;
            assert done.getDeclaredMethods().length > 0;
        } catch (VerifyError | ClassFormatError ex) {
            //<editor-fold desc="Output the bytecode for debug purposes" defaultstate="collapsed">
            final File debug = new File("target/test-failures/Test.class");
            System.out.println(Arrays.toString(file.constant_pool()));
            System.out.println(Arrays.toString(file.attributes()));
            file.debug(System.out); // todo
            try {
                debug.getParentFile().mkdirs();
                debug.createNewFile();
                try (OutputStream stream = new FileOutputStream(debug)) {
                    stream.write(file.binary());
                }
            } catch (IOException _) {
            }
            //</editor-fold>
            throw new AssertionError(ex);
        }
        final Class<?>[] parameters = new Class[builder.parameters().length];
        for (int i = 0; i < builder.parameters().length; i++) parameters[i] = builder.parameters()[i].toClass();
        final Method found = done.getDeclaredMethod("test", parameters);
        found.setAccessible(true);
        return found;
    }

    protected void check(MethodBuilder builder, Object value) {
        try {
            final Object result = this.compileForTest(builder).invoke(null);
            assert Objects.equals(result, value) : result;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
        final MethodBuilder method = builder.method().erasure(Erasure.of(boolean[].class, "test", String.class,
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
