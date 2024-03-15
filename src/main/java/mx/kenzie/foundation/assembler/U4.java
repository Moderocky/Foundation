package mx.kenzie.foundation.assembler;

import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an unsigned 4-byte value.
 */
public record U4(long value) implements UVec, Data, RecordConstant {

    public U4(int value) {
        this(Integer.toUnsignedLong(value));
    }

    @Override
    public int length() {
        return 4;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write(this.binary());
    }

    @Override
    public byte[] binary() {
        final int value = (int) this.value;
        return new byte[] {(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) (value)};
    }

}
