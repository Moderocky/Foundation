package mx.kenzie.foundation.assembler;

import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Represents an unsigned 4-byte value.
 */
public record U4(long value) implements UVec, Data, RecordConstant {

    private static final U4[] unsignedCache = new U4[128];

    public U4(int value) {
        this(Integer.toUnsignedLong(value));
    }

    public static U4 valueOf(int i) {
        if (i > 127) return new U4(i);
        final U4 current = unsignedCache[i];
        if (current == null) return unsignedCache[i] = new U4(i);
        return current;
    }

    public static U4 lengthOf(UVec... vecs) {
        int length = 0;
        for (UVec vec : vecs) {
            length += vec.length();
        }
        return U4.valueOf(length);
    }

    public static U4 fromSigned(int i) {
        return new U4(i);
    }

    public static U4 fromSigned(float f) {
        return new U4(Float.floatToIntBits(f));
    }

    @Override
    public int length() {
        return 4;
    }

    @Override
    public byte[] binary() {
        final int value = (int) this.value;
        return new byte[] {(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) (value)};
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write(this.binary());
    }

    public int intValue() {
        return (int) value;
    }

    public float floatValue() {
        return Float.intBitsToFloat(this.intValue());
    }

    public long longValue(U4 lowBits) {
        return ByteBuffer.allocate(Long.BYTES).putInt(this.intValue()).putInt(lowBits.intValue()).getLong(0);
    }

    public double doubleValue(U4 lowBits) {
        return ByteBuffer.allocate(Double.BYTES).putInt(this.intValue()).putInt(lowBits.intValue()).getDouble(0);
    }

}
