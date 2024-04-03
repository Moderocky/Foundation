package org.valross.foundation.assembler.vector;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class U2Test {

    @Test
    public void value() throws IOException {
        for (short i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++) {
            final U2 u2 = new U2(Short.toUnsignedInt(i));
            assert u2.shortValue() == i;
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            u2.write(stream);
            assert Arrays.equals(u2.binary(), stream.toByteArray());
            final byte[] bytes = new byte[] {(byte) (i >>> 8), (byte) i};
            assert Arrays.equals(bytes, u2.binary());
            assert i != 129 || bytes[0] == 0 && bytes[1] == -127;
        }

    }

}