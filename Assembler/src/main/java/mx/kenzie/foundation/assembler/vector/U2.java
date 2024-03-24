package mx.kenzie.foundation.assembler.vector;

import mx.kenzie.foundation.assembler.Data;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an unsigned 4-byte value.
 */
public record U2(int value)
    implements UVec, Data, RecordConstant {

    private static final U2[] unsignedCache = new U2[128];
    public static final U2 ZERO = U2.valueOf(0);

    public U2(short value) {
        this(Short.toUnsignedInt(value));
    }

    public static U2 valueOf(short s) {
        return valueOf((int) s);
    }

    public static U2 valueOf(int i) {
        if (i < 0 || i > unsignedCache.length) return new U2(i);
        final U2 current = unsignedCache[i];
        if (current == null) return unsignedCache[i] = new U2(i);
        return current;
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public byte[] binary() {
        final short value = (short) this.value;
        return new byte[] {
            (byte) (value >>> 8),
            (byte) (value)
        };
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        final short value = (short) this.value;
        stream.write((value >>> 8));
        stream.write(value);
    }

    public short shortValue() {
        return (short) value;
    }

}
