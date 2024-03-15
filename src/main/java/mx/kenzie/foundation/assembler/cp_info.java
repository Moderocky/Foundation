package mx.kenzie.foundation.assembler;

import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

public interface cp_info extends Data {

    U1 CONSTANT_Class = new U1(7), CONSTANT_Fieldref = new U1(9), CONSTANT_Methodref = new U1(10), CONSTANT_InterfaceMethodref = new U1(11), CONSTANT_String = new U1(8), CONSTANT_Integer = new U1(3), CONSTANT_Float = new U1(4), CONSTANT_Long = new U1(5), CONSTANT_Double = new U1(6), CONSTANT_NameAndType = new U1(12), CONSTANT_Utf8 = new U1(1);

    static cp_info of(String string) {
        return utf8_info.of(string);
    }

    U1 tag();

    UVec info();

    @Override
    default void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.tag().write(stream);
        this.info().write(stream);
    }

    @Override
    default byte[] binary() { // inefficient but subclasses should deal with this
        return UVec.of(this.tag(), this.info()).binary();
    }

}

record utf8_info(int length, byte[] data) implements cp_info, UVec, RecordConstant {

    private static final int MAX_LENGTH = 65535;

    public utf8_info(byte[] data) {
        this(data.length, data);
    }

    public static utf8_info of(String string) {
        final int length = string.length();
        byte[] data = new byte[(length / 16 + 1) * 16];
        int pointer = -1;
        for (int i = 0; i < length; ++i) {
            if (pointer >= data.length - 3) data = copy(data, (data.length / 16 + 1) * 16);
            final char c = string.charAt(i);
            if (c >= 0x0001 && c <= 0x007F) {
                data[++pointer] = (byte) c;
            } else if (c <= 0x07FF) {
                data[++pointer] = (byte) (0xC0 | c >> 6 & 0x1F);
                data[++pointer] = (byte) (0x80 | c & 0x3F);
            } else {
                data[++pointer] = (byte) (0xE0 | c >> 12 & 0xF);
                data[++pointer] = (byte) (0x80 | c >> 6 & 0x3F);
                data[++pointer] = (byte) (0x80 | c & 0x3F);
            }
        }
        if (pointer > MAX_LENGTH) throw new IllegalArgumentException("UTF-8 string is too long.");
        if (++pointer != data.length) data = copy(data, pointer);
        return new utf8_info(data);
    }

    private static byte[] copy(byte[] data, int length) {
        final byte[] copy = new byte[length];
        System.arraycopy(data, 0, copy, 0, Math.min(data.length, length));
        return copy;
    }

    @Override
    public U1 tag() {
        return CONSTANT_Utf8;
    }

    @Override
    public UVec info() {
        return UVec.of(new U2(length), data);
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        stream.write(CONSTANT_Utf8.value());
        stream.write((byte) length >>> 8);
        stream.write((byte) length);
        stream.write(data);
    }

    @Override
    public byte[] binary() {
        return this.info().binary();
    }

}
