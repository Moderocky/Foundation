package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.Loader;
import mx.kenzie.foundation.assembler.ClassFile;
import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.tool.MethodBuilder;
import mx.kenzie.foundation.assembler.tool.MethodBuilderTest;
import mx.kenzie.foundation.detail.Type;
import org.jetbrains.annotations.Contract;
import org.junit.Test;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static mx.kenzie.foundation.assembler.code.OpCode.*;
import static mx.kenzie.foundation.assembler.tool.Access.PUBLIC;
import static mx.kenzie.foundation.assembler.tool.Access.STATIC;
import static mx.kenzie.foundation.detail.Version.JAVA_21;
import static mx.kenzie.foundation.detail.Version.RELEASE;

public class OpCodeTest extends MethodBuilderTest {

    @Contract(pure = true)
    protected MethodBuilder method() {
        return new ClassFileBuilder(JAVA_21, RELEASE).setType(Type.of("org.example", "Test")).method()
                                                     .setModifiers(PUBLIC, STATIC).named("test");
    }

    @Contract(pure = true)
    protected Method compileForTest(MethodBuilder builder) throws NoSuchMethodException {
        final Loader loader = Loader.createDefault();
        final ClassFile file = builder.exit().build();
        final Class<?> done = this.load(loader, file, Type.of("org.example", "Test"));
        assert done != null;
        assert done.getDeclaredMethods().length > 0;
        final Class<?>[] parameters = new Class[builder.parameters().length];
        for (int i = 0; i < builder.parameters().length; i++) parameters[i] = builder.parameters()[i].toClass();
        final Method found = done.getDeclaredMethod("test", parameters);
        found.setAccessible(true);
        return found;
    }

    @Test
    public void mnemonic() throws NoSuchFieldException, IllegalAccessException {
        for (OpCode code : Codes.getAllOpcodes()) {
            assert OpCode.class.isAssignableFrom(OpCode.class.getDeclaredField(code.mnemonic()).getType());
        }
        for (Field field : OpCode.class.getDeclaredFields()) {
            field.setAccessible(true);
            final OpCode code = (OpCode) field.get(null);
            final byte b = Codes.class.getDeclaredField(code.mnemonic()).getByte(null);
            assert b == code.code();
        }
    }

    @Test
    public void code() {
        assert opcodes().length == 202;
        for (OpCode code : opcodes()) {
            assert code != null;
            assert Byte.toUnsignedInt(code.code()) < 202;
        }
    }

    @Test
    public void length() {
        for (OpCode code : opcodes()) {
            assert code != null;
            assert (code.hasFixedLength() && code.length() > 0) || code.length() == -1;
        }
    }

    @Test
    public void hasFixedLength() {
    }

    @Test
    public void raw() {
    }

    @Test
    public void testRaw() {
    }

    @Test
    public void testAALOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().code()
                                       .write(ICONST_1, ANEWARRAY.type(Object.class), DUP, ICONST_0, LDC.value("foo")
                                           , AASTORE)
                                       .write(ICONST_0, AALOAD, ARETURN).exit().returns(Object.class)).invoke(null)
                   .equals("foo");
        assert this.compileForTest(this.method().code().write(ALOAD_0, ICONST_0, AALOAD, ARETURN).exit()
                                       .parameters(Object[].class).returns(Object.class))
                   .invoke(null, (Object) new Object[] {"hello"}).equals("hello");
    }

    @Test
    public void testAASTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_2, ANEWARRAY.type(Object.class), DUP, ICONST_0, LDC.value("foo"), AASTORE);
        builder.code().write(DUP, ICONST_1, LDC.value("bar"), AASTORE);
        builder.code().write(ICONST_1, AALOAD, ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("bar");
        assert this.compileForTest(this.method().code()
                                       .write(ALOAD_0, DUP, ICONST_0, LDC.value("hello"), AASTORE, ARETURN).exit()
                                       .parameters(Object[].class).returns(Object.class))
                   .invoke(null, (Object) new Object[1]) instanceof Object[] objects && objects[0].equals("hello");
    }

    @Test
    public void testACONSTNULL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ACONST_NULL, ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null) == null;
    }

    @Test
    public void testALOAD0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("blob"), ASTORE_0);
        builder.code().write(ALOAD_0, ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("blob");
    }

    @Test
    public void testALOAD1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("test"), ASTORE_1);
        builder.code().write(ALOAD_1, ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("test");
    }

    @Test
    public void testALOAD2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("test"), ASTORE_2);
        builder.code().write(ALOAD_2, ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("test");
    }

    @Test
    public void testALOAD3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("test"), ASTORE_3);
        builder.code().write(ALOAD_3, ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("test");
    }

    @Test
    public void testALOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("test"), DUP, ASTORE.var(4), ASTORE.var(0));
        builder.code().write(ALOAD.var(4), ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("test");
    }

    @Test
    public void testANEWARRAY() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value(2), ANEWARRAY.type(int[].class));
        builder.code().write(ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null) instanceof int[][] ints && ints.length == 2;
    }

    @Test
    public void testARETURN() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("test"), ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("test");
    }

    @Test
    public void testARRAYLENGTH() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value(2), ANEWARRAY.type(int[].class));
        builder.code().write(ARRAYLENGTH, IRETURN);
        builder.returns(int.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals(2);
    }

    @Test
    public void testASTORE0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("test"), ASTORE_0);
        builder.code().write(ALOAD_0, ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("test");
    }

    @Test
    public void testASTORE1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("test"), ASTORE_1);
        builder.code().write(ALOAD_1, ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("test");
    }

    @Test
    public void testASTORE2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("test"), ASTORE_2);
        builder.code().write(ALOAD_2, ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("test");
    }

    @Test
    public void testASTORE3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("test"), ASTORE_3);
        builder.code().write(ALOAD_3, ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("test");
    }

    @Test
    public void testASTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("test"), DUP, ASTORE.var(4), ASTORE.var(0));
        builder.code().write(ALOAD.var(4), ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("test");
    }

    @Test(expected = Error.class)
    public void testATHROW() throws Throwable {
        final MethodBuilder builder = this.method();
        builder.code().write(NEW.type(Error.class), DUP, INVOKESPECIAL.constructor(Error.class));
        builder.code().write(ATHROW);
        final Method method = this.compileForTest(builder);
        try {
            method.invoke(null);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    @Test
    public void testBALOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.type(byte.class), DUP, ICONST_0, BIPUSH.value(5), BASTORE);
        builder.code().write(ICONST_0, BALOAD, IRETURN);
        builder.returns(int.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals(5);
    }

    @Test
    public void testBASTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.primitive(8), DUP, ICONST_0, BIPUSH.value(3), BASTORE);
        builder.code().write(ICONST_0, BALOAD, IRETURN);
        builder.returns(int.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals(3);
    }

    @Test
    public void testBIPUSH() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert BIPUSH.length() == 2 && BIPUSH.hasFixedLength();
        for (int i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++) {
            assert BIPUSH.value(i).code() == Codes.BIPUSH;
            assert BIPUSH.value((byte) i).code() == Codes.BIPUSH;
        }
        for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
            final MethodBuilder builder = this.method();
            builder.code().write(ICONST_1, NEWARRAY.primitive(8), DUP, ICONST_0, BIPUSH.value(i), BASTORE);
            builder.code().write(ICONST_0, BALOAD, IRETURN);
            builder.returns(byte.class);
            final Method method = this.compileForTest(builder);
            assert method.invoke(null).equals((byte) i) : method.invoke(null) + " != " + i;
        }
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.primitive(8), DUP, ICONST_0);
        builder.code().write(BIPUSH.value(255), BASTORE);
        builder.code().write(ICONST_0, BALOAD, IRETURN);
        builder.returns(byte.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals((byte) -1) : method.invoke(null) + " != " + -1;
    }

    @Test
    public void testCALOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.type(char.class), DUP, ICONST_0, BIPUSH.value('t'), CASTORE);
        builder.code().write(ICONST_0, CALOAD, IRETURN);
        builder.returns(char.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals('t');
    }

    @Test
    public void testCASTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.type(char.class), DUP, ICONST_0, BIPUSH.value('c'), CASTORE);
        builder.code().write(ICONST_0, CALOAD, IRETURN);
        builder.returns(char.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals('c');
    }

    @Test
    public void testCHECKCAST() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(LDC.value("test"), CHECKCAST.type(Object.class), CHECKCAST.type(String.class));
        builder.code().write(ARETURN);
        builder.returns(String.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("test");
    }

    @Test
    public void testD2F() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(float.class).code().write(DCONST_0, D2F, FRETURN).exit())
                   .invoke(null).equals(0F);
        assert this.compileForTest(this.method().returns(float.class).code().write(DCONST_1, D2F, FRETURN).exit())
                   .invoke(null).equals(1.0F);
        assert this.compileForTest(this.method().returns(float.class).code().write(LDC.value(1.5D), D2F, FRETURN)
                                       .exit()).invoke(null).equals(1.5F);
        assert this.compileForTest(this.method().returns(float.class).code().write(LDC.value(-4.1D), D2F, FRETURN)
                                       .exit()).invoke(null).equals(-4.1F);
    }

    @Test
    public void testD2I() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(DCONST_0, D2I, IRETURN).exit())
                   .invoke(null).equals(0);
        assert this.compileForTest(this.method().returns(int.class).code().write(DCONST_1, D2I, IRETURN).exit())
                   .invoke(null).equals(1);
        assert this.compileForTest(this.method().returns(int.class).code().write(LDC.value(1.5D), D2I, IRETURN).exit())
                   .invoke(null).equals(1);
        assert this.compileForTest(this.method().returns(int.class).code().write(LDC.value(-4.1D), D2I, IRETURN).exit())
                   .invoke(null).equals(-4);
    }

    @Test
    public void testD2L() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code().write(DCONST_0, D2L, LRETURN).exit())
                   .invoke(null).equals(0L);
        assert this.compileForTest(this.method().returns(long.class).code().write(DCONST_1, D2L, LRETURN).exit())
                   .invoke(null).equals(1L);
        assert this.compileForTest(this.method().returns(long.class).code().write(LDC.value(1.5D), D2L, LRETURN).exit())
                   .invoke(null).equals(1L);
        assert this.compileForTest(this.method().returns(long.class).code().write(LDC.value(-4.1D), D2L, LRETURN)
                                       .exit()).invoke(null).equals(-4L);
    }

    @Test
    public void testDADD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final double a = random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), b =
                random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
            assert this.compileForTest(this.method().returns(double.class).code()
                                           .write(LDC.value(a), LDC.value(b), DADD, DRETURN).exit()).invoke(null)
                       .equals(a + b);
        }
    }

    @Test
    public void testDALOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.type(double.class), DUP, ICONST_0, BIPUSH.value(3), I2D, DASTORE);
        builder.code().write(ICONST_0, DALOAD, DRETURN);
        builder.returns(double.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals(3D);
    }

    @Test
    public void testDASTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.type(double.class), DUP, ICONST_0, LDC.value(-10.0), DASTORE);
        builder.code().write(ICONST_0, DALOAD, DRETURN);
        builder.returns(double.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals(-10D);
    }

    @Test
    public void testDCMPG() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final double a = random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), b =
                random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), DCMPG, IRETURN).exit()).invoke(null)
                       .equals(Double.compare(a, b));
        }
    }

    @Test
    public void testDCMPL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final double a = random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), b =
                random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), DCMPL, IRETURN).exit()).invoke(null)
                       .equals(Double.compare(a, b));
        }
    }

    @Test
    public void testDCONST0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(double.class).code().write(DCONST_0, DRETURN).exit())
                   .invoke(null).equals(0D);
    }

    @Test
    public void testDCONST1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(double.class).code().write(DCONST_1, DRETURN).exit())
                   .invoke(null).equals(1D);
    }

    @Test
    public void testDDIV() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final double a = random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), b =
                random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
            assert this.compileForTest(this.method().returns(double.class).code()
                                           .write(LDC.value(a), LDC.value(b), DDIV, DRETURN).exit()).invoke(null)
                       .equals(a / b);
        }
    }

    @Test
    public void testDLOAD0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().parameters(double.class, double.class, double.class, double.class)
                                       .returns(double.class).code().write(DLOAD_0, DRETURN).exit())
                   .invoke(null, 0D, 1D, 2D, 3D).equals(0D);
        assert this.compileForTest(this.method().returns(double.class).code()
                                       .write(DCONST_1, DSTORE_0, DLOAD_0, DRETURN).exit()).invoke(null).equals(1D);
    }

    @Test
    public void testDLOAD1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().parameters(int.class, double.class, double.class, double.class)
                                       .returns(double.class).code().write(DLOAD_1, DRETURN).exit())
                   .invoke(null, 0, 1D, 2D, 3D).equals(1D);
        assert this.compileForTest(this.method().returns(double.class).code()
                                       .write(DCONST_1, DSTORE_1, DLOAD_1, DRETURN).exit()).invoke(null).equals(1D);
    }

    @Test
    public void testDLOAD2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().parameters(double.class, double.class, double.class, double.class)
                                       .returns(double.class).code().write(DLOAD_2, DRETURN).exit())
                   .invoke(null, 0D, 1D, 2D, 3D).equals(1D);
        assert this.compileForTest(this.method().returns(double.class).code()
                                       .write(DCONST_1, DSTORE_2, DLOAD_2, DRETURN).exit()).invoke(null).equals(1D);
    }

    @Test
    public void testDLOAD3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().parameters(int.class, double.class, double.class, double.class)
                                       .returns(double.class).code().write(DLOAD_3, DRETURN).exit())
                   .invoke(null, 0, 1D, 2D, 3D).equals(2D);
        assert this.compileForTest(this.method().returns(double.class).code()
                                       .write(DCONST_1, DSTORE_3, DLOAD_3, DRETURN).exit()).invoke(null).equals(1D);
    }

    @Test
    public void testDLOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final double value = random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
            assert this.compileForTest(this.method().returns(double.class).code()
                                           .write(LDC.value(value), DSTORE.var(i), DLOAD.var(i), DRETURN).exit())
                       .invoke(null).equals(value);
        }
    }

    @Test
    public void testDMUL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final double a = random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), b =
                random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
            assert this.compileForTest(this.method().returns(double.class).code()
                                           .write(LDC.value(a), LDC.value(b), DMUL, DRETURN).exit()).invoke(null)
                       .equals(a * b);
        }
    }

    @Test
    public void testDNEG() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final double value = random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
            assert this.compileForTest(this.method().returns(double.class).code().write(LDC.value(value), DNEG, DRETURN)
                                           .exit()).invoke(null).equals(-value);
        }
    }

    @Test
    public void testDREM() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final double a = random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), b =
                random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
            assert this.compileForTest(this.method().returns(double.class).code()
                                           .write(LDC.value(a), LDC.value(b), DREM, DRETURN).exit()).invoke(null)
                       .equals(a % b);
        }
    }

    @Test
    public void testDRETURN() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(double.class).code().write(DCONST_1, DRETURN).exit())
                   .invoke(null).equals(1D);
    }

    @Test
    public void testDSTORE0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(double.class).code()
                                       .write(DCONST_1, DSTORE_0, DLOAD_0, DRETURN).exit()).invoke(null).equals(1D);
    }

    @Test
    public void testDSTORE1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(double.class).code()
                                       .write(DCONST_1, DSTORE_1, DLOAD_1, DRETURN).exit()).invoke(null).equals(1D);
    }

    @Test
    public void testDSTORE2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(double.class).code()
                                       .write(DCONST_1, DSTORE_2, DLOAD_2, DRETURN).exit()).invoke(null).equals(1D);
    }

    @Test
    public void testDSTORE3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(double.class).code()
                                       .write(DCONST_1, DSTORE_3, DLOAD_3, DRETURN).exit()).invoke(null).equals(1D);
    }

    @Test
    public void testDSTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final double value = random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
            assert this.compileForTest(this.method().returns(double.class).code()
                                           .write(LDC.value(value), DSTORE.var(i), DLOAD.var(i), DRETURN).exit())
                       .invoke(null).equals(value);
        }
    }

    @Test
    public void testDSUB() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final double a = random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), b =
                random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
            assert this.compileForTest(this.method().returns(double.class).code()
                                           .write(LDC.value(a), LDC.value(b), DSUB, DRETURN).exit()).invoke(null)
                       .equals(a - b);
        }
    }

    @Test
    public void testDUP() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(Object.class).code()
                                       .write(NEW.type(Object.class), DUP, INVOKESPECIAL.constructor(Object.class),
                                              ARETURN)
                                       .exit()).invoke(null) != null;
        assert this.compileForTest(this.method().returns(Object.class).code()
                                       .write(LDC.value("hello"), DUP, POP, ARETURN).exit()).invoke(null)
                   .equals("hello");
    }

    @Test
    public void testDUPX1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(Object.class).code()
                                       .write(LDC.value("goodbye"), LDC.value("hello"), DUP_X1, POP2, ARETURN).exit())
                   .invoke(null).equals("hello");
    }

    @Test
    public void testDUPX2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(Object.class).code()
                                       .write(LDC.value("goodbye"), ICONST_0, LDC.value("hello"), DUP_X2, POP2, POP,
                                              ARETURN)
                                       .exit()).invoke(null).equals("hello");
        assert this.compileForTest(this.method().returns(Object.class).code()
                                       .write(LCONST_0, LDC.value("hello"), DUP_X2, POP, POP2, ARETURN).exit())
                   .invoke(null).equals("hello");
    }

    @Test
    public void testDUP2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(Object.class).code()
                                       .write(LDC.value("hello"), LDC.value("there"), DUP2, POP2, POP, ARETURN).exit())
                   .invoke(null).equals("hello");
        assert this.compileForTest(this.method().returns(long.class).code().write(LCONST_1, DUP2, POP2, LRETURN).exit())
                   .invoke(null).equals(1L);
    }

    @Test
    public void testDUP2X1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code()
                                       .write(BIPUSH.value(5), LCONST_1, DUP2_X1, POP2, IRETURN).exit()).invoke(null)
                   .equals(5);
        assert this.compileForTest(this.method().returns(long.class).code()
                                       .write(BIPUSH.value(5), LCONST_1, DUP2_X1, POP2, POP, LRETURN).exit())
                   .invoke(null).equals(1L);
        assert this.compileForTest(this.method().returns(int.class).code()
                                       .write(BIPUSH.value(3), BIPUSH.value(5), LCONST_1, DUP2_X1, POP2, POP, POP2,
                                              IRETURN)
                                       .exit()).invoke(null).equals(3);
    }

    @Test
    public void testDUP2X2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code()
                                       .write(LCONST_0, LCONST_1, DUP2_X2, POP2, LRETURN).exit()).invoke(null)
                   .equals(0L);
        assert this.compileForTest(this.method().returns(long.class).code()
                                       .write(LCONST_0, LCONST_1, DUP2_X2, POP2, POP2, LRETURN).exit()).invoke(null)
                   .equals(1L);
        assert this.compileForTest(this.method().returns(int.class).code()
                                       .write(ICONST_M1, LCONST_0, LCONST_1, DUP2_X2, POP2, POP2, POP2, IRETURN).exit())
                   .invoke(null).equals(-1);
    }

    @Test
    public void testF2D() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(double.class).code().write(FCONST_0, F2D, DRETURN).exit())
                   .invoke(null).equals(0.0D);
        assert this.compileForTest(this.method().returns(double.class).code().write(FCONST_1, F2D, DRETURN).exit())
                   .invoke(null).equals(1.0D);
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final float value = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(double.class).code().write(LDC.value(value), F2D, DRETURN)
                                           .exit()).invoke(null).equals((double) value);
        }
    }

    @Test
    public void testF2I() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(FCONST_0, F2I, IRETURN).exit())
                   .invoke(null).equals(0);
        assert this.compileForTest(this.method().returns(int.class).code().write(FCONST_1, F2I, IRETURN).exit())
                   .invoke(null).equals(1);
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final float value = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(int.class).code().write(LDC.value(value), F2I, IRETURN)
                                           .exit()).invoke(null).equals((int) value);
        }
    }

    @Test
    public void testF2L() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code().write(FCONST_0, F2L, LRETURN).exit())
                   .invoke(null).equals(0L);
        assert this.compileForTest(this.method().returns(long.class).code().write(FCONST_1, F2L, LRETURN).exit())
                   .invoke(null).equals(1L);
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final float value = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(long.class).code().write(LDC.value(value), F2L, LRETURN)
                                           .exit()).invoke(null).equals((long) value);
        }
    }

    @Test
    public void testFADD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final float a = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE), b = random.nextFloat(Float.MIN_VALUE,
                                                                                                     Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code()
                                           .write(LDC.value(a), LDC.value(b), FADD, FRETURN).exit()).invoke(null)
                       .equals(a + b);
        }
    }

    @Test
    public void testFALOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.type(float.class), DUP, ICONST_0, BIPUSH.value(3), I2F, FASTORE);
        builder.code().write(ICONST_0, FALOAD, FRETURN);
        builder.returns(float.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals(3F);
    }

    @Test
    public void testFASTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.type(float.class), DUP, ICONST_0, LDC.value(-5F), FASTORE);
        builder.code().write(ICONST_0, FALOAD, FRETURN);
        builder.returns(float.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals(-5F);
    }

    @Test
    public void testFCMPG() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final float a = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE), b = random.nextFloat(Float.MIN_VALUE,
                                                                                                     Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), FCMPG, IRETURN).exit()).invoke(null)
                       .equals(Float.compare(a, b));
        }
    }

    @Test
    public void testFCMPL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final float a = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE), b = random.nextFloat(Float.MIN_VALUE,
                                                                                                     Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), FCMPG, IRETURN).exit()).invoke(null)
                       .equals(Float.compare(a, b));
        }
    }

    @Test
    public void testFCONST0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(float.class).code().write(FCONST_0, FRETURN).exit())
                   .invoke(null).equals(0F);
    }

    @Test
    public void testFCONST1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(float.class).code().write(FCONST_1, FRETURN).exit())
                   .invoke(null).equals(1F);
    }

    @Test
    public void testFCONST2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(float.class).code().write(FCONST_2, FRETURN).exit())
                   .invoke(null).equals(2F);
    }

    @Test
    public void testFDIV() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final float a = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE), b = random.nextFloat(Float.MIN_VALUE,
                                                                                                     Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code()
                                           .write(LDC.value(a), LDC.value(b), FDIV, FRETURN).exit()).invoke(null)
                       .equals(a / b);
        }
    }

    @Test
    public void testFLOAD0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (int i = 0; i < 10; i++) {
            assert this.compileForTest(this.method().parameters(float.class, float.class, float.class, float.class)
                                           .returns(float.class).code().write(FLOAD_0, FRETURN).exit())
                       .invoke(null, 0F, 1F, 2F, 3F).equals(0F);
        }
    }

    @Test
    public void testFLOAD1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (int i = 0; i < 10; i++) {
            assert this.compileForTest(this.method().parameters(float.class, float.class, float.class, float.class)
                                           .returns(float.class).code().write(FLOAD_1, FRETURN).exit())
                       .invoke(null, 0F, 1F, 2F, 3F).equals(1F);
        }
    }

    @Test
    public void testFLOAD2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (int i = 0; i < 10; i++) {
            assert this.compileForTest(this.method().parameters(float.class, float.class, float.class, float.class)
                                           .returns(float.class).code().write(FLOAD_2, FRETURN).exit())
                       .invoke(null, 0F, 1F, 2F, 3F).equals(2F);
        }
    }

    @Test
    public void testFLOAD3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (int i = 0; i < 10; i++) {
            assert this.compileForTest(this.method().parameters(float.class, float.class, float.class, float.class)
                                           .returns(float.class).code().write(FLOAD_3, FRETURN).exit())
                       .invoke(null, 0F, 1F, 2F, 3F).equals(3F);
        }
    }

    @Test
    public void testFLOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final float value = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code()
                                           .write(LDC.value(value), FSTORE.var(i), FLOAD.var(i), FRETURN).exit())
                       .invoke(null).equals(value);
        }
    }

    @Test
    public void testFMUL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final float a = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE), b = random.nextFloat(Float.MIN_VALUE,
                                                                                                     Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code()
                                           .write(LDC.value(a), LDC.value(b), FMUL, FRETURN).exit()).invoke(null)
                       .equals(a * b);
        }
    }

    @Test
    public void testFNEG() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final float a = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE), b = -a;
            assert this.compileForTest(this.method().returns(float.class).code().write(LDC.value(a), FNEG, FRETURN)
                                           .exit()).invoke(null).equals(b);
        }
    }

    @Test
    public void testFREM() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final float a = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE), b = random.nextFloat(Float.MIN_VALUE,
                                                                                                     Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code()
                                           .write(LDC.value(a), LDC.value(b), FREM, FRETURN).exit()).invoke(null)
                       .equals(a % b);
        }
    }

    @Test
    public void testFRETURN() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(float.class).code().write(FCONST_2, FRETURN).exit())
                   .invoke(null).equals(2F);
    }

    @Test
    public void testFSTORE0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final float value = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code()
                                           .write(LDC.value(value), FSTORE_0, FLOAD_0, FRETURN).exit()).invoke(null)
                       .equals(value);
        }
    }

    @Test
    public void testFSTORE1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final float value = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code()
                                           .write(LDC.value(value), FSTORE_1, FLOAD_1, FRETURN).exit()).invoke(null)
                       .equals(value);
        }
    }

    @Test
    public void testFSTORE2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final float value = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code()
                                           .write(LDC.value(value), FSTORE_2, FLOAD_2, FRETURN).exit()).invoke(null)
                       .equals(value);
        }
    }

    @Test
    public void testFSTORE3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final float value = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code()
                                           .write(LDC.value(value), FSTORE_3, FLOAD_3, FRETURN).exit()).invoke(null)
                       .equals(value);
        }
    }

    @Test
    public void testFSTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final float value = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code()
                                           .write(LDC.value(value), FSTORE.var(i), FLOAD.var(i), FRETURN).exit())
                       .invoke(null).equals(value);
        }
    }

    @Test
    public void testFSUB() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final float a = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE), b = random.nextFloat(Float.MIN_VALUE,
                                                                                                     Float.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code()
                                           .write(LDC.value(a), LDC.value(b), FSUB, FRETURN).exit()).invoke(null)
                       .equals(a - b);
        }
    }

    @Test
    public void testGETFIELD()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");

        ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, type).addModifiers(PUBLIC).field().addModifiers(PUBLIC)
                                                                      .named("test").ofType(Object.class).exit()
                                                                      .constructor().addModifiers(PUBLIC).code()
                                                                      .write(ALOAD_0,
                                                                             INVOKESPECIAL.constructor(Object.class),
                                                                             RETURN)
                                                                      .exit().exit().method().named("test")
                                                                      .addModifiers(PUBLIC).returns(Object.class).code()
                                                                      .write(ALOAD_0, GETFIELD.field(type, "test",
                                                                                                     Object.class),
                                                                             ARETURN)
                                                                      .exit().exit();

        final ClassFile file = builder.build();

        final Class<?> clazz = loader.loadClass(type.getTypeName(), file.binary());
        final Object instance = clazz.getDeclaredConstructor().newInstance();

        final Method method = clazz.getDeclaredMethod("test");
        assert method.invoke(instance) == null;
    }

    @Test
    public void testGETSTATIC() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(PrintStream.class).code()
                                       .write(GETSTATIC.field(System.class, "out", PrintStream.class), ARETURN).exit())
                   .invoke(null).equals(System.out);
    }

    @Test
    public void testGOTO() {
        // FIXME: impl GOTO
    }

    @Test
    public void testGOTOW() {
        // FIXME: impl GOTOW
    }

    @Test
    public void testI2B() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int value = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            assert this.compileForTest(this.method().returns(byte.class).code().write(LDC.value(value), I2B, IRETURN)
                                           .exit()).invoke(null).equals((byte) value);
        }
    }

    @Test
    public void testI2C() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int value = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            assert this.compileForTest(this.method().returns(char.class).code().write(LDC.value(value), I2C, IRETURN)
                                           .exit()).invoke(null).equals((char) value);
        }
    }

    @Test
    public void testI2D() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int value = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            assert this.compileForTest(this.method().returns(double.class).code().write(LDC.value(value), I2D, DRETURN)
                                           .exit()).invoke(null).equals((double) value);
        }
    }

    @Test
    public void testI2F() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int value = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code().write(LDC.value(value), I2F, FRETURN)
                                           .exit()).invoke(null).equals((float) value);
        }
    }

    @Test
    public void testI2L() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int value = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            assert this.compileForTest(this.method().returns(long.class).code().write(LDC.value(value), I2L, LRETURN)
                                           .exit()).invoke(null).equals((long) value);
        }
    }

    @Test
    public void testI2S() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int value = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            assert this.compileForTest(this.method().returns(short.class).code().write(LDC.value(value), I2S, IRETURN)
                                           .exit()).invoke(null).equals((short) value);
        }
    }

    @Test
    public void testIADD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE), b = random.nextInt(Integer.MIN_VALUE,
                                                                                                   Integer.MAX_VALUE);
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), IADD, IRETURN).exit()).invoke(null)
                       .equals(a + b);
        }
    }

    @Test
    public void testIALOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.type(int.class), DUP, ICONST_0, ICONST_3, IASTORE);
        builder.code().write(ICONST_0, IALOAD, IRETURN);
        builder.returns(int.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals(3);
    }

    @Test
    public void testIAND() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE), b = random.nextInt(Integer.MIN_VALUE,
                                                                                                   Integer.MAX_VALUE);
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), IAND, IRETURN).exit()).invoke(null)
                       .equals(a & b);
        }
    }

    @Test
    public void testIASTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.type(int.class), DUP, ICONST_0, BIPUSH.value(5), IASTORE);
        builder.code().write(ICONST_0, IALOAD, IRETURN);
        builder.returns(int.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals(5);
    }

    @Test
    public void testICONSTM1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_M1, IRETURN).exit())
                   .invoke(null).equals(-1);
    }

    @Test
    public void testICONST0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_0, IRETURN).exit()).invoke(null)
                   .equals(0);
    }

    @Test
    public void testICONST1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_1, IRETURN).exit()).invoke(null)
                   .equals(1);
    }

    @Test
    public void testICONST2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_2, IRETURN).exit()).invoke(null)
                   .equals(2);
    }

    @Test
    public void testICONST3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_3, IRETURN).exit()).invoke(null)
                   .equals(3);
    }

    @Test
    public void testICONST4() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_4, IRETURN).exit()).invoke(null)
                   .equals(4);
    }

    @Test
    public void testICONST5() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_5, IRETURN).exit()).invoke(null)
                   .equals(5);
    }

    @Test
    public void testIDIV() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE), b = random.nextInt(Integer.MIN_VALUE,
                                                                                                   Integer.MAX_VALUE);
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), IDIV, IRETURN).exit()).invoke(null)
                       .equals(a / b);
        }
    }

    @Test
    public void testIFACMPEQ() {
    }

    @Test
    public void testIFACMPNE() {
    }

    @Test
    public void testIFICMPEQ() {
    }

    @Test
    public void testIFICMPNE() {
    }

    @Test
    public void testIFICMPLT() {
    }

    @Test
    public void testIFICMPGE() {
    }

    @Test
    public void testIFICMPGT() {
    }

    @Test
    public void testIFICMPLE() {
    }

    @Test
    public void testIFEQ() {
    }

    @Test
    public void testIFNE() {
    }

    @Test
    public void testIFLT() {
    }

    @Test
    public void testIFGE() {
    }

    @Test
    public void testIFGT() {
    }

    @Test
    public void testIFLE() {
    }

    @Test
    public void testIFNONNULL() {
    }

    @Test
    public void testIFNULL() {
    }

    @Test
    public void testIINC() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final int value = random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE);
            assert this.compileForTest(this.method().returns(int.class).parameters(int.class).code()
                                           .write(IINC.var(0, value), ILOAD_0, IRETURN).exit()).invoke(null, 0)
                       .equals(value) : this.compileForTest(this.method().returns(int.class).parameters(int.class)
                                                                .code().write(IINC.var(0, value), ILOAD_0, IRETURN)
                                                                .exit()).invoke(null, 0);
        }
    }

    @Test
    public void testILOAD0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_0, ISTORE_0, ILOAD_0, IRETURN)
                                       .exit()).invoke(null).equals(0);
    }

    @Test
    public void testILOAD1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_1, ISTORE_1, ILOAD_1, IRETURN)
                                       .exit()).invoke(null).equals(1);
    }

    @Test
    public void testILOAD2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_2, ISTORE_2, ILOAD_2, IRETURN)
                                       .exit()).invoke(null).equals(2);
    }

    @Test
    public void testILOAD3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_3, ISTORE_3, ILOAD_3, IRETURN)
                                       .exit()).invoke(null).equals(3);
    }

    @Test
    public void testILOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final int value = random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(value), ISTORE.var(i), ILOAD.var(i), IRETURN).exit())
                       .invoke(null).equals(value);
        }
    }

    @Test
    public void testIMUL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt(), b = random.nextInt();
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), IMUL, IRETURN).exit()).invoke(null)
                       .equals(a * b);
        }
    }

    @Test
    public void testINEG() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt();
            assert this.compileForTest(this.method().returns(int.class).code().write(LDC.value(a), INEG, IRETURN)
                                           .exit()).invoke(null).equals(-a);
        }
    }

    @Test
    public void testINSTANCEOF()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");

        ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, type).addModifiers(PUBLIC).field().addModifiers(PUBLIC)
                                                                      .named("test").ofType(Object.class).exit()
                                                                      .constructor().addModifiers(PUBLIC).code()
                                                                      .write(ALOAD_0,
                                                                             INVOKESPECIAL.constructor(Object.class),
                                                                             RETURN)
                                                                      .exit().exit().method().named("test")
                                                                      .addModifiers(PUBLIC).returns(boolean.class)
                                                                      .code()
                                                                      .write(ALOAD_0, INSTANCEOF.type(type), IRETURN)
                                                                      .exit().exit();

        final ClassFile file = builder.build();

        final Class<?> clazz = loader.loadClass(type.getTypeName(), file.binary());
        final Object instance = clazz.getDeclaredConstructor().newInstance();

        final Method method = clazz.getDeclaredMethod("test");
        assert method.invoke(instance).equals(true);
    }

    @Test
    public void testINVOKEDYNAMIC() {
    }

    @Test
    public void testINVOKEINTERFACE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().code().setTrackStack(false).stackSize(2)
                                       .write(NEW.type(ArrayList.class), DUP,
                                              INVOKESPECIAL.constructor(ArrayList.class),
                                              INVOKEINTERFACE.method(List.class, int.class, "size"), IRETURN)
                                       .exit().returns(int.class)).invoke(null).equals(0);
    }

    @Test
    public void testINVOKESPECIAL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(Object.class).code()
                                       .write(NEW.type(Object.class), DUP, INVOKESPECIAL.constructor(Object.class),
                                              ARETURN)
                                       .exit()).invoke(null) != null;
    }

    @Test
    public void testINVOKESTATIC() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(Runtime.class).code().setTrackStack(false).stackSize(1)
                                       .write(INVOKESTATIC.method(Runtime.class, Runtime.class, "getRuntime"), ARETURN)
                                       .exit()).invoke(null).equals(Runtime.getRuntime());
    }

    @Test
    public void testINVOKEVIRTUAL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String expected = "the spanish inquisition";

        class Thing {

            @Override
            public String toString() {
                return expected;
            }

        }
        ;

        assert this.compileForTest(this.method().parameters(Thing.class).returns(String.class).code()
                                       .setTrackStack(false).stackSize(1)
                                       .write(ALOAD_0, INVOKEVIRTUAL.method(Object.class, String.class, "toString"),
                                              ARETURN)
                                       .exit()).invoke(null, new Thing()).equals(expected);
    }

    @Test
    public void testIOR() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt(), b = random.nextInt();
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), IOR, IRETURN).exit()).invoke(null)
                       .equals(a | b);
        }
    }

    @Test
    public void testIREM() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt(), b = random.nextInt();
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), IREM, IRETURN).exit()).invoke(null)
                       .equals(a % b);
        }
    }

    @Test
    public void testIRETURN() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_2, IRETURN).exit()).invoke(null)
                   .equals(2);
    }

    @Test
    public void testISHL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt(), b = random.nextInt(32);
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), ISHL, IRETURN).exit()).invoke(null)
                       .equals(a << b);
        }
    }

    @Test
    public void testISHR() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt(), b = random.nextInt(32);
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), ISHR, IRETURN).exit()).invoke(null)
                       .equals(a >> b);
        }
    }

    @Test
    public void testISTORE0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_0, ISTORE_0, ILOAD_0, IRETURN)
                                       .exit()).invoke(null).equals(0);
    }

    @Test
    public void testISTORE1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_1, ISTORE_1, ILOAD_1, IRETURN)
                                       .exit()).invoke(null).equals(1);
    }

    @Test
    public void testISTORE2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_2, ISTORE_2, ILOAD_2, IRETURN)
                                       .exit()).invoke(null).equals(2);
    }

    @Test
    public void testISTORE3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(ICONST_3, ISTORE_3, ILOAD_3, IRETURN)
                                       .exit()).invoke(null).equals(3);
    }

    @Test
    public void testISTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final int value = random.nextInt();
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(value), ISTORE.var(i), ILOAD.var(i), IRETURN).exit())
                       .invoke(null).equals(value);
        }
    }

    @Test
    public void testISUB() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt(), b = random.nextInt();
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), ISUB, IRETURN).exit()).invoke(null)
                       .equals(a - b);
        }
    }

    @Test
    public void testIUSHR() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt(), b = random.nextInt(32);
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), IUSHR, IRETURN).exit()).invoke(null)
                       .equals(a >>> b);
        }
    }

    @Test
    public void testIXOR() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final int a = random.nextInt(), b = random.nextInt();
            assert this.compileForTest(this.method().returns(int.class).code()
                                           .write(LDC.value(a), LDC.value(b), IXOR, IRETURN).exit()).invoke(null)
                       .equals(a ^ b);
        }
    }

    @Test
    public void testJSR() {
    }

    @Test
    public void testJSRW() {
    }

    @Test
    public void testL2D() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long value = random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
            assert this.compileForTest(this.method().returns(double.class).code().write(LDC.value(value), L2D, DRETURN)
                                           .exit()).invoke(null).equals((double) value);
        }
    }

    @Test
    public void testL2F() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long value = random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
            assert this.compileForTest(this.method().returns(float.class).code().write(LDC.value(value), L2F, FRETURN)
                                           .exit()).invoke(null).equals((float) value);
        }
    }

    @Test
    public void testL2I() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long value = random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
            assert this.compileForTest(this.method().returns(int.class).code().write(LDC.value(value), L2I, IRETURN)
                                           .exit()).invoke(null).equals((int) value);
        }
    }

    @Test
    public void testLADD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong(), b = random.nextLong();
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(a), LDC.value(b), LADD, LRETURN).exit()).invoke(null)
                       .equals(a + b);
        }
    }

    @Test
    public void testLALOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.type(long.class), DUP, ICONST_0, LDC.value(3L), LASTORE);
        builder.code().write(ICONST_0, LALOAD, LRETURN);
        builder.returns(long.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals(3L);
    }

    @Test
    public void testLAND() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong(), b = random.nextLong();
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(a), LDC.value(b), LAND, LRETURN).exit()).invoke(null)
                       .equals(a & b);
        }
    }

    @Test
    public void testLASTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, NEWARRAY.type(long.class), DUP, ICONST_0, LDC.value(5L), LASTORE);
        builder.code().write(ICONST_0, LALOAD, LRETURN);
        builder.returns(long.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals(5L);
    }

    @Test
    public void testLCMP() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code()
                                       .write(LDC.value(1L), LDC.value(2L), LCMP, IRETURN).exit()).invoke(null)
                   .equals(-1);
        assert this.compileForTest(this.method().returns(int.class).code()
                                       .write(LDC.value(2L), LDC.value(1L), LCMP, IRETURN).exit()).invoke(null)
                   .equals(1);
        assert this.compileForTest(this.method().returns(int.class).code()
                                       .write(LDC.value(1L), LDC.value(1L), LCMP, IRETURN).exit()).invoke(null)
                   .equals(0);
    }

    @Test
    public void testLCONST0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code().write(LCONST_0, LRETURN).exit())
                   .invoke(null).equals(0L);
    }

    @Test
    public void testLCONST1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code().write(LCONST_1, LRETURN).exit())
                   .invoke(null).equals(1L);
    }

    @Test
    public void testLDC() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(int.class).code().write(LDC.value(1), IRETURN).exit())
                   .invoke(null).equals(1);
    }

    @Test
    public void testLDCW() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final MethodBuilder methodBuilder = this.method();
        final ClassFileBuilder classBuilder = methodBuilder.exit();
        final var storage = classBuilder.helper();
        for (int i = 0; i < 1024; storage.constant(i++)) ;
        assert this.compileForTest(methodBuilder.returns(int.class).code().write(LDC.value(-1), IRETURN).exit())
                   .invoke(null).equals(-1);
    }

    @Test
    public void testLDC2W() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code().write(LDC.value(1L), LRETURN).exit())
                   .invoke(null).equals(1L);
    }

    @Test
    public void testLDIV() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong(), b = random.nextLong();
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(a), LDC.value(b), LDIV, LRETURN).exit()).invoke(null)
                       .equals(a / b);
        }
    }

    @Test
    public void testLLOAD0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code().write(LCONST_0, LSTORE_0, LLOAD_0, LRETURN)
                                       .exit()).invoke(null).equals(0L);
    }

    @Test
    public void testLLOAD1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code().write(LCONST_1, LSTORE_1, LLOAD_1, LRETURN)
                                       .exit()).invoke(null).equals(1L);
    }

    @Test
    public void testLLOAD2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code()
                                       .write(LDC.value(2L), LSTORE_2, LLOAD_2, LRETURN).exit()).invoke(null)
                   .equals(2L);
    }

    @Test
    public void testLLOAD3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code()
                                       .write(LDC.value(3L), LSTORE_3, LLOAD_3, LRETURN).exit()).invoke(null)
                   .equals(3L);
    }

    @Test
    public void testLLOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final long value = random.nextLong();
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(value), LSTORE.var(i), LLOAD.var(i), LRETURN).exit())
                       .invoke(null).equals(value);
        }
    }

    @Test
    public void testLMUL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong(), b = random.nextLong();
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(a), LDC.value(b), LMUL, LRETURN).exit()).invoke(null)
                       .equals(a * b);
        }
    }

    @Test
    public void testLNEG() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong();
            assert this.compileForTest(this.method().returns(long.class).code().write(LDC.value(a), LNEG, LRETURN)
                                           .exit()).invoke(null).equals(-a);
        }
    }

    @Test
    public void testLOOKUPSWITCH() {
    }

    @Test
    public void testLOR() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong(), b = random.nextLong();
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(a), LDC.value(b), LOR, LRETURN).exit()).invoke(null)
                       .equals(a | b);
        }
    }

    @Test
    public void testLREM() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong(), b = random.nextLong();
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(a), LDC.value(b), LREM, LRETURN).exit()).invoke(null)
                       .equals(a % b);
        }
    }

    @Test
    public void testLRETURN() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code().write(LCONST_0, LRETURN).exit())
                   .invoke(null).equals(0L);
    }

    @Test
    public void testLSHL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong(), b = random.nextInt(64);
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(a), LDC.value((int) b), LSHL, LRETURN).exit()).invoke(null)
                       .equals(a << b);
        }
    }

    @Test
    public void testLSHR() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong(), b = random.nextInt(64);
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(a), LDC.value((int) b), LSHR, LRETURN).exit()).invoke(null)
                       .equals(a >> b);
        }
    }

    @Test
    public void testLSTORE0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code().write(LCONST_0, LSTORE_0, LLOAD_0, LRETURN)
                                       .exit()).invoke(null).equals(0L);
    }

    @Test
    public void testLSTORE1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code().write(LCONST_1, LSTORE_1, LLOAD_1, LRETURN)
                                       .exit()).invoke(null).equals(1L);
    }

    @Test
    public void testLSTORE2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code()
                                       .write(LDC.value(2L), LSTORE_2, LLOAD_2, LRETURN).exit()).invoke(null)
                   .equals(2L);
    }

    @Test
    public void testLSTORE3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(long.class).code()
                                       .write(LDC.value(3L), LSTORE_3, LLOAD_3, LRETURN).exit()).invoke(null)
                   .equals(3L);
    }

    @Test
    public void testLSTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            final long value = random.nextLong();
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(value), LSTORE.var(i), LLOAD.var(i), LRETURN).exit())
                       .invoke(null).equals(value);
        }
    }

    @Test
    public void testLSUB() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong(), b = random.nextLong();
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(a), LDC.value(b), LSUB, LRETURN).exit()).invoke(null)
                       .equals(a - b);
        }
    }

    @Test
    public void testLUSHR() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong(), b = random.nextInt(64);
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(a), LDC.value((int) b), LUSHR, LRETURN).exit()).invoke(null)
                       .equals(a >>> b);
        }
    }

    @Test
    public void testLXOR() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final long a = random.nextLong(), b = random.nextLong();
            assert this.compileForTest(this.method().returns(long.class).code()
                                           .write(LDC.value(a), LDC.value(b), LXOR, LRETURN).exit()).invoke(null)
                       .equals(a ^ b);
        }
    }

    @Test
    public void testMONITORENTER() {
    }

    @Test
    public void testMONITOREXIT() {
    }

    @Test
    public void testMULTIANEWARRAY() {
    }

    @Test
    public void testNEW() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert Objects.nonNull(this.compileForTest(this.method().returns(Object.class).code()
                                                       .write(NEW.type(Object.class), DUP,
                                                              INVOKESPECIAL.constructor(Object.class), ARETURN)
                                                       .exit()).invoke(null));
    }

    @Test
    public void testNEWARRAY() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().code()
                                       .write(ICONST_1, NEWARRAY.type(int.class), DUP, ICONST_0, ICONST_M1, IASTORE,
                                              ARETURN)
                                       .exit().returns(int[].class))
                   .invoke(null) instanceof int[] ints && ints.length == 1 && ints[0] == -1;
        for (int i = 4; i < 12; i++) {
            final Object result = this.compileForTest(this.method().code()
                                                          .write(ICONST_1, NEWARRAY.primitive(i), ARETURN).exit()
                                                          .returns(Object.class)).invoke(null);
            assert result != null && result.getClass().isArray() && result.getClass().componentType().isPrimitive();
            assert result.getClass().componentType() == switch (i) {
                case 4 -> boolean.class;
                case 5 -> char.class;
                case 6 -> float.class;
                case 7 -> double.class;
                case 8 -> byte.class;
                case 9 -> short.class;
                case 10 -> int.class;
                case 11 -> long.class;
                default -> throw new AssertionError(i);
            };
        }
    }

    @Test
    public void testNOP() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(String.class).code()
                                       .write(NOP, NOP, LDC.value("hello"), NOP, NOP, ARETURN).exit()).invoke(null)
                   .equals("hello");
    }

    @Test
    public void testPOP() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(float.class).code()
                                       .write(LDC.value(1.5F), ICONST_3, POP, FRETURN).exit()).invoke(null)
                   .equals(1.5F);
        assert this.compileForTest(this.method().returns(String.class).code()
                                       .write(LDC.value("hello"), LDC.value("goodbye"), ICONST_M1, POP, POP, ARETURN)
                                       .exit()).invoke(null).equals("hello");
    }

    @Test
    public void testPOP2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(float.class).code()
                                       .write(LDC.value(1.5F), ICONST_3, FCONST_0, POP2, FRETURN).exit()).invoke(null)
                   .equals(1.5F);
        assert this.compileForTest(this.method().returns(String.class).code()
                                       .write(LDC.value("hello"), LDC.value("goodbye"), ICONST_M1, POP2, ARETURN)
                                       .exit()).invoke(null).equals("hello");
        assert this.compileForTest(this.method().returns(String.class).code()
                                       .write(LDC.value("hello"), LCONST_0, POP2, ARETURN).exit()).invoke(null)
                   .equals("hello");
    }

    @Test
    public void testPUTFIELD()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");
        final ClassFileBuilder classBuilder = new ClassFileBuilder(JAVA_21, type).addModifiers(PUBLIC);

        classBuilder.field().addModifiers(PUBLIC).named("hash").ofType(int.class).exit().constructor()
                    .addModifiers(PUBLIC).code().write(ALOAD_0, INVOKESPECIAL.constructor(Object.class), RETURN).exit()
                    .exit().method().named("test").setModifiers(PUBLIC).code()
                    .write(ALOAD_0, DUP, ICONST_1, PUTFIELD.field(type, "hash", int.class), GETFIELD.field(type,
                                                                                                           "hash",
                                                                                                           int.class)
                        , IRETURN)
                    .exit().returns(int.class);

        final ClassFile classFile = classBuilder.build();
        final Class<?> clazz = loader.loadClass(type.getTypeName(), classFile.binary());
        assert clazz.getDeclaredMethod("test").invoke(clazz.getConstructor().newInstance()).equals(1);
    }

    @Test
    public void testPUTSTATIC() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");
        final ClassFileBuilder classBuilder = new ClassFileBuilder(JAVA_21, type).addModifiers(PUBLIC);

        classBuilder.field().addModifiers(PUBLIC, STATIC).named("hash").ofType(int.class).exit().method().named("test")
                    .setModifiers(PUBLIC, STATIC).code()
                    .write(ICONST_1, PUTSTATIC.field(type, "hash", int.class), GETSTATIC.field(type, "hash",
                                                                                               int.class), IRETURN)
                    .exit().returns(int.class);

        final ClassFile classFile = classBuilder.build();
        final Class<?> clazz = loader.loadClass(type.getTypeName(), classFile.binary());
        assert clazz.getDeclaredMethod("test").invoke(null).equals(1);
    }

    @Test
    public void testRET() {
    }

    @Test
    public void testRETURN() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().code().write(RETURN).exit()).invoke(null) == null;
    }

    @Test
    public void testSALOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().code()
                                       .write(ICONST_1, NEWARRAY.type(short.class), DUP, ICONST_0,
                                              LDC.value((short) 3), SASTORE, ICONST_0, SALOAD, IRETURN)
                                       .exit().returns(int.class)).invoke(null).equals(3);
    }

    @Test
    public void testSASTORE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().code()
                                       .write(ICONST_1, NEWARRAY.type(short.class), DUP, ICONST_0,
                                              LDC.value((short) 3), SASTORE, ICONST_0, SALOAD, IRETURN)
                                       .exit().returns(int.class)).invoke(null).equals(3);
    }

    @Test
    public void testSIPUSH() {
        for (int i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++) {
            final short value = (short) i;
            final ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.put(0, new byte[] {(byte) (value >> 8), (byte) (value)});
            final short shr = buffer.getShort(0);
            assert shr == value : shr + " != " + value;
            buffer.put(0, new byte[] {(byte) (value >>> 8), (byte) (value)});
            final short ushr = buffer.getShort(0);
            assert ushr == shr : shr + " != " + ushr;
            assert SIPUSH.value(value).code() == Codes.SIPUSH;
        }
    }

    @Test
    public void testSWAP() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().returns(float.class).code()
                                       .write(LDC.value(1.5F), ICONST_3, SWAP, FRETURN).exit()).invoke(null)
                   .equals(1.5F);
        assert this.compileForTest(this.method().returns(String.class).code()
                                       .write(LDC.value("hello"), ICONST_M1, SWAP, ARETURN).exit()).invoke(null)
                   .equals("hello");
    }

    @Test
    public void testTABLESWITCH() {
    }

    @Test
    public void testWIDE() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final int slot = 7175;

        final CodeBuilder code = this.method().returns(int.class).code().setTrackStack(false).stackSize(1);
        code.notifyMaxLocalIndex(slot);

        assert this.compileForTest(code.write(ICONST_1, ISTORE.var(slot), ILOAD.var(slot), IRETURN).exit()).invoke(null)
                   .equals(1);
    }

}