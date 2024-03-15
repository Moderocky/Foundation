package mx.kenzie.foundation.assembler;

import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an unsigned 4-byte value.
 */
public record U1(short value) implements UVec, Data, RecordConstant {

    public U1(byte value) {
        this((short) (((short) value) & 0xFF));
    }

    public U1(int value) {
        this((byte) value);
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write((byte) value);
    }

    @Override
    public byte[] binary() {
        return new byte[] {(byte) value};
    }

}
