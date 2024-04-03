package org.valross.foundation.assembler.constant;

import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;
import java.util.Arrays;
import java.util.Objects;

public record Utf8Info(String value, byte[] data) implements ConstantPoolInfo, UVec, RecordConstant {

    private static final int MAX_LENGTH = 65535;

    public Utf8Info(byte[] data) {
        this(null, data);
    }

    public static Utf8Info of(String string) {
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
        return new Utf8Info(string, data);
    }

    private static byte[] copy(byte[] data, int length) {
        final byte[] copy = new byte[length];
        System.arraycopy(data, 0, copy, 0, Math.min(data.length, length));
        return copy;
    }

    @Override
    public int length() {
        return data.length + 3;
    }

    @Override
    public ConstantType<Utf8Info, String> tag() {
        return UTF8;
    }

    @Override
    public UVec info() {
        return UVec.of(U2.valueOf(data.length), data);
    }

    @Override
    public boolean is(Constable object) {
        if (value != null) return value.equals(object);
        return object instanceof String string &&
            this.equals(Utf8Info.of(string));
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        UTF8.write(stream);
        final short length = (short) data.length;
        stream.write((length) >>> 8);
        stream.write(length);
        stream.write(data);
    }

    @Override
    public byte[] binary() {
        return ConstantPoolInfo.super.binary();
    }

    @Override
    public int sort() {
        return 3;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (value != null && object instanceof Utf8Info utf8Info && utf8Info.value != null)
            return value.equals(utf8Info.value);
        return object instanceof Utf8Info utf8Info
            && data.length == utf8Info.data.length && Arrays.equals(data, utf8Info.data);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(data.length) + Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return "Utf8Info[" +
            "length=" + data.length +
            ", value='" + value + '\'' +
            ", data=" + Arrays.toString(data) +
            ']';
    }

}
