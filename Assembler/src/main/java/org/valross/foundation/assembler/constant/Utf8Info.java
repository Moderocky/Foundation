package org.valross.foundation.assembler.constant;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.UVec;

import java.io.DataInput;
import java.io.DataInputStream;
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
        return object instanceof CharSequence sequence && this.unpack().contentEquals(sequence);
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
    public String unpack() {
        return value != null ? value : this.unpack0();
    }

    /**
     * Behaviour is adapted from {@link DataInputStream#readUTF(DataInput)}.
     * Used to unpack a string from the byte array, rather than our reference.
     *
     * @return The string contained in the bytes.
     */
    String unpack0() {
        final int length = data.length;
        final char[] characters = new char[length];
        int character, code2, code3;
        int count = 0, characterCount = 0;
        //<editor-fold desc="Assume all characters are in the 'simple' range, i.e. 1 byte -> 1 char"
        // defaultstate="collapsed">
        while (count < length) {
            character = (int) data[count] & 0xff;
            if (character > 127) break; // we found a non-simple character
            count++;
            characters[characterCount++] = (char) character;
        }
        //</editor-fold>
        //<editor-fold desc="For any bytes left, we use the advanced mode" defaultstate="collapsed">
        while (count < length) {
            character = (int) data[count] & 0xff;
            switch (character >> 4) {
                case 0, 1, 2, 3, 4, 5, 6, 7 -> { // 0xxxxxxx is simple char range
                    ++count;
                    characters[characterCount++] = (char) character;
                }
                case 12, 13 -> { // 110x xxxx | 10xx xxxx is two-byte char range
                    count += 2;
                    if (count > length)
                        throw new IllegalStateException("Malformed input: partial character at end");
                    code2 = data[count - 1];
                    if ((code2 & 0xC0) != 0x80)
                        throw new RuntimeException("Malformed input around byte " + count);
                    characters[characterCount++] = (char) (((character & 0x1F) << 6) |
                        (code2 & 0x3F));
                }
                case 14 -> { // 1110 xxxx | 10xx xxxx | 10xx xxxx is three-byte char range
                    count += 3;
                    if (count > length)
                        throw new IllegalStateException("Malformed input: partial character at end");
                    code2 = data[count - 2];
                    code3 = data[count - 1];
                    if (((code2 & 0xC0) != 0x80) || ((code3 & 0xC0) != 0x80))
                        throw new IllegalStateException("Malformed input around byte " + (count - 1));
                    characters[characterCount++] = (char) (((character & 0x0F) << 12) |
                        ((code2 & 0x3F) << 6) |
                        ((code3 & 0x3F) << 0));
                }
                default -> throw new IllegalStateException("Malformed input around byte " + count);
                // 10xx xxxx | 1111 xxxx is ???
            }
        }
        //</editor-fold>
        return new String(characters, 0, characterCount);
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
            ", value='" + this.unpack() + '\'' +
            ", data=" + Arrays.toString(data) +
            ']';
    }

}
