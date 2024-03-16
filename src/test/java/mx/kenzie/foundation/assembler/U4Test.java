package mx.kenzie.foundation.assembler;

import org.junit.Test;

public class U4Test {

    @Test
    public void fromSignedNegative() {
        assert -42 == (int) Integer.toUnsignedLong(-42);
        final int value = -42;
        final U4 u4 = U4.fromSigned(value);
        final int result = u4.intValue();
        assert value == result;
    }

}