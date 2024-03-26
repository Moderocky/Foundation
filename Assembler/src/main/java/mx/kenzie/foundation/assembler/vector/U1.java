package mx.kenzie.foundation.assembler.vector;

import mx.kenzie.foundation.assembler.Data;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an unsigned 4-byte value.
 */
public record U1(short value) implements UVec, Data, RecordConstant {

    private static final U1[] unsignedCache = new U1[256];
    public static final U1 ZERO = U1.valueOf(0);

    public U1(byte value) {
        this((short) (((short) value) & 0xFF));
    }

    public U1(int value) {
        this((byte) value);
    }

    public static U1 valueOf(short s) {
        return valueOf((int) s);
    }

    public static U1 valueOf(int i) {
        if (i < 0 || i > unsignedCache.length) i = (short) Math.max(0, Math.min(unsignedCache.length, i));
        final U1 current = unsignedCache[i];
        if (current == null) return unsignedCache[i] = new U1(i);
        return current;
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public byte[] binary() {
        return new byte[] {(byte) value};
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write((byte) value);
    }

}
