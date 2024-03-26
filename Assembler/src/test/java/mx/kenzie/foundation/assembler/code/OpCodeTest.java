package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.Loader;
import mx.kenzie.foundation.assembler.ClassFile;
import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.tool.MethodBuilder;
import mx.kenzie.foundation.assembler.tool.MethodBuilderTest;
import mx.kenzie.foundation.detail.Type;
import org.jetbrains.annotations.Contract;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

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
        final Method found = done.getDeclaredMethod("test");
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
        assert OpCode.opcodes().length == 202;
        for (OpCode code : OpCode.opcodes()) {
            assert code != null;
            assert Byte.toUnsignedInt(code.code()) < 202;
        }
    }

    @Test
    public void length() {
        for (OpCode code : OpCode.opcodes()) {
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
        final MethodBuilder builder = this.method();
        builder.code().write(ICONST_1, ANEWARRAY.type(Object.class), DUP, ICONST_0, LDC.value("foo"), AASTORE);
        builder.code().write(ICONST_0, AALOAD, ARETURN);
        builder.returns(Object.class);
        final Method method = this.compileForTest(builder);
        assert method.invoke(null).equals("foo");
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
    public void testDADD() {
    }

    @Test
    public void testDALOAD() {
    }

    @Test
    public void testDASTORE() {
    }

    @Test
    public void testDCMPG() {
    }

    @Test
    public void testDCMPL() {
    }

    @Test
    public void testDCONST0() {
    }

    @Test
    public void testDCONST1() {
    }

    @Test
    public void testDDIV() {
    }

    @Test
    public void testDLOAD0() {
    }

    @Test
    public void testDLOAD1() {
    }

    @Test
    public void testDLOAD2() {
    }

    @Test
    public void testDLOAD3() {
    }

    @Test
    public void testDLOAD() {
    }

    @Test
    public void testDMUL() {
    }

    @Test
    public void testDNEG() {
    }

    @Test
    public void testDREM() {
    }

    @Test
    public void testDRETURN() {
    }

    @Test
    public void testDSTORE0() {
    }

    @Test
    public void testDSTORE1() {
    }

    @Test
    public void testDSTORE2() {
    }

    @Test
    public void testDSTORE3() {
    }

    @Test
    public void testDSTORE() {
    }

    @Test
    public void testDSUB() {
    }

    @Test
    public void testDUP() {
    }

    @Test
    public void testDUPX1() {
    }

    @Test
    public void testDUPX2() {
    }

    @Test
    public void testDUP2() {
    }

    @Test
    public void testDUP2X1() {
    }

    @Test
    public void testDUP2X2() {
    }

    @Test
    public void testF2D() {
    }

    @Test
    public void testF2I() {
    }

    @Test
    public void testF2L() {
    }

    @Test
    public void testFADD() {
    }

    @Test
    public void testFALOAD() {
    }

    @Test
    public void testFASTORE() {
    }

    @Test
    public void testFCMPG() {
    }

    @Test
    public void testFCMPL() {
    }

    @Test
    public void testFCONST0() {
    }

    @Test
    public void testFCONST1() {
    }

    @Test
    public void testFCONST2() {
    }

    @Test
    public void testFDIV() {
    }

    @Test
    public void testFLOAD0() {
    }

    @Test
    public void testFLOAD1() {
    }

    @Test
    public void testFLOAD2() {
    }

    @Test
    public void testFLOAD3() {
    }

    @Test
    public void testFLOAD() {
    }

    @Test
    public void testFMUL() {
    }

    @Test
    public void testFNEG() {
    }

    @Test
    public void testFREM() {
    }

    @Test
    public void testFRETURN() {
    }

    @Test
    public void testFSTORE0() {
    }

    @Test
    public void testFSTORE1() {
    }

    @Test
    public void testFSTORE2() {
    }

    @Test
    public void testFSTORE3() {
    }

    @Test
    public void testFSTORE() {
    }

    @Test
    public void testFSUB() {
    }

    @Test
    public void testGETFIELD() {
    }

    @Test
    public void testGETSTATIC() {
    }

    @Test
    public void testGOTO() {
    }

    @Test
    public void testGOTOW() {
    }

    @Test
    public void testI2B() {
    }

    @Test
    public void testI2C() {
    }

    @Test
    public void testI2D() {
    }

    @Test
    public void testI2F() {
    }

    @Test
    public void testI2L() {
    }

    @Test
    public void testI2S() {
    }

    @Test
    public void testIADD() {
    }

    @Test
    public void testIALOAD() {
    }

    @Test
    public void testIAND() {
    }

    @Test
    public void testIASTORE() {
    }

    @Test
    public void testICONSTM1() {
    }

    @Test
    public void testICONST0() {
    }

    @Test
    public void testICONST1() {
    }

    @Test
    public void testICONST2() {
    }

    @Test
    public void testICONST3() {
    }

    @Test
    public void testICONST4() {
    }

    @Test
    public void testICONST5() {
    }

    @Test
    public void testIDIV() {
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
    public void testIINC() {
    }

    @Test
    public void testILOAD0() {
    }

    @Test
    public void testILOAD1() {
    }

    @Test
    public void testILOAD2() {
    }

    @Test
    public void testILOAD3() {
    }

    @Test
    public void testILOAD() {
    }

    @Test
    public void testIMUL() {
    }

    @Test
    public void testINEG() {
    }

    @Test
    public void testINSTANCEOF() {
    }

    @Test
    public void testINVOKEDYNAMIC() {
    }

    @Test
    public void testINVOKEINTERFACE() {
    }

    @Test
    public void testINVOKESPECIAL() {
    }

    @Test
    public void testINVOKESTATIC() {
    }

    @Test
    public void testINVOKEVIRTUAL() {
    }

    @Test
    public void testIOR() {
    }

    @Test
    public void testIREM() {
    }

    @Test
    public void testIRETURN() {
    }

    @Test
    public void testISHL() {
    }

    @Test
    public void testISHR() {
    }

    @Test
    public void testISTORE0() {
    }

    @Test
    public void testISTORE1() {
    }

    @Test
    public void testISTORE2() {
    }

    @Test
    public void testISTORE3() {
    }

    @Test
    public void testISTORE() {
    }

    @Test
    public void testISUB() {
    }

    @Test
    public void testIUSHR() {
    }

    @Test
    public void testIXOR() {
    }

    @Test
    public void testJSR() {
    }

    @Test
    public void testJSRW() {
    }

    @Test
    public void testL2D() {
    }

    @Test
    public void testL2F() {
    }

    @Test
    public void testL2I() {
    }

    @Test
    public void testLADD() {
    }

    @Test
    public void testLALOAD() {
    }

    @Test
    public void testLAND() {
    }

    @Test
    public void testLASTORE() {
    }

    @Test
    public void testLCMP() {
    }

    @Test
    public void testLCONST0() {
    }

    @Test
    public void testLCONST1() {
    }

    @Test
    public void testLDC() {
    }

    @Test
    public void testLDCW() {
    }

    @Test
    public void testLDC2W() {
    }

    @Test
    public void testLDIV() {
    }

    @Test
    public void testLLOAD0() {
    }

    @Test
    public void testLLOAD1() {
    }

    @Test
    public void testLLOAD2() {
    }

    @Test
    public void testLLOAD3() {
    }

    @Test
    public void testLLOAD() {
    }

    @Test
    public void testLMUL() {
    }

    @Test
    public void testLNEG() {
    }

    @Test
    public void testLOOKUPSWITCH() {
    }

    @Test
    public void testLOR() {
    }

    @Test
    public void testLREM() {
    }

    @Test
    public void testLRETURN() {
    }

    @Test
    public void testLSHL() {
    }

    @Test
    public void testLSHR() {
    }

    @Test
    public void testLSTORE0() {
    }

    @Test
    public void testLSTORE1() {
    }

    @Test
    public void testLSTORE2() {
    }

    @Test
    public void testLSTORE3() {
    }

    @Test
    public void testLSTORE() {
    }

    @Test
    public void testLSUB() {
    }

    @Test
    public void testLUSHR() {
    }

    @Test
    public void testLXOR() {
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
    public void testNEW() {
    }

    @Test
    public void testNEWARRAY() {
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
    public void testPUTFIELD() {
    }

    @Test
    public void testPUTSTATIC() {
    }

    @Test
    public void testRET() {
    }

    @Test
    public void testRETURN() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert this.compileForTest(this.method().code()
                                       .write(RETURN).exit()).invoke(null) == null;
    }

    @Test
    public void testSALOAD() {
    }

    @Test
    public void testSASTORE() {
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
    public void testWIDE() {
    }

}