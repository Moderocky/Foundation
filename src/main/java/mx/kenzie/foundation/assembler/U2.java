package mx.kenzie.foundation.assembler;

import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an unsigned 4-byte value.
 */
public record U2(int value)
    implements UVec, Data, RecordConstant {

    public U2(short value) {
        this(Short.toUnsignedInt(value));
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        final short value = (short) this.value;
        stream.write((value >>> 8));
        stream.write(value);
    }

    @Override
    public byte[] binary() {
        final short value = (short) this.value;
        return new byte[] {
            (byte) (value >>> 8),
            (byte) (value)
        };
    }

}
