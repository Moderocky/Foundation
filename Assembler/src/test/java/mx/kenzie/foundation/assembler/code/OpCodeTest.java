package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.MethodBuilderTest;
import org.junit.Test;

import java.lang.reflect.Field;

public class OpCodeTest extends MethodBuilderTest {

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

}