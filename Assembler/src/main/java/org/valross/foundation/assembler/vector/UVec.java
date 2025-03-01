package org.valross.foundation.assembler.vector;

import org.valross.constantine.Constant;
import org.valross.constantine.Constantive;
import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.ArraySegments;
import org.valross.foundation.assembler.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.constant.Constable;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Represents a quantity of unsigned binary data.
 * This will typically be an array of an unspecified number type (e.g. unsigned bytes, unsigned shorts)
 * but the type isn't specified. The length is counted in (unsigned) bytes.
 * As such, the data can be interpreted as any unsigned type, by dividing the data length by the unit length.
 */
public interface UVec extends Data, Constantive {

    static ConVec of() {
        //<editor-fold desc="Empty vector" defaultstate="collapsed">
        return new ConVec() {
            @Override
            public int length() {
                return 0;
            }

            @Override
            public byte[] binary() {
                return new byte[0];
            }

            @Override
            public void write(OutputStream stream) {
            }

            @Override
            public Constable[] serial() {
                return new Constable[0];
            }

            @Override
            public Class<?>[] canonicalParameters() {
                return new Class[0];
            }
        };
        //</editor-fold>
    }

    static ConVec of(byte[] bytes) {
        return new UnsignedVector(copy(bytes));
    }

    static UVec of(Data... data) {
        return new CoVec(data);
    }

    static UVec of(Object... objects) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            for (Object object : objects) {
                if (object instanceof Data data) data.write(stream);
                else if (object instanceof Data[] data) for (Data datum : data) datum.write(stream);
                else if (object instanceof byte[] data) stream.write(data);
                else if (object instanceof Number number) stream.write(number.byteValue());
            }
            return of(stream.toByteArray());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

    }

    private static byte[] copy(byte[] bytes) {
        final byte[] copy = new byte[bytes.length];
        System.arraycopy(bytes, 0, copy, 0, bytes.length);
        return copy;
    }

    private static void copy(byte[] source, byte[] target) {
        System.arraycopy(source, 0, target, 0, Math.min(source.length, target.length));
    }

    @Override
    default void write(OutputStream stream) throws IOException {
        stream.write(this.binary());
    }

    default void copyTo(Object array) {
        if (array instanceof byte[] data) copy(this.binary(), data);
        else ArraySegments.instance().arrayCopy(this.binary(), array);
    }

    /**
     * Turns a complex unsigned vector (e.g. one that's representing multiple pieces of data)
     * into a "flat" vector (representing a single block of data)
     * This is much more efficient if you are planning to perform multiple index operations.
     *
     * @return The same vector, if it was already flat, or a new Unsigned Vector.
     */
    default ConVec flatten() {
        if (this instanceof UnsignedVector vector) return vector;
        return new UnsignedVector(this.binary());
    }

    default byte byteValue() {
        final byte[] bytes = new byte[Byte.BYTES];
        this.copyTo(bytes);
        return bytes[0];
    }

    default short shortValue() {
        final byte[] bytes = new byte[Short.BYTES];
        this.copyTo(bytes);
        return ByteBuffer.wrap(bytes).getShort(0);
    }

    default int intValue() {
        final byte[] bytes = new byte[Integer.BYTES];
        this.copyTo(bytes);
        return ByteBuffer.wrap(bytes).getInt(0);
    }

    default float floatValue() {
        final byte[] bytes = new byte[Float.BYTES];
        this.copyTo(bytes);
        return ByteBuffer.wrap(bytes).getFloat(0);
    }

    default long longValue() {
        final byte[] bytes = new byte[Long.BYTES];
        this.copyTo(bytes);
        return ByteBuffer.wrap(bytes).getLong(0);
    }

    default double doubleValue() {
        final byte[] bytes = new byte[Double.BYTES];
        this.copyTo(bytes);
        return ByteBuffer.wrap(bytes).getDouble(0);
    }

    interface ConVec extends Constant, UVec {

        @Override
        default ConVec constant() {
            return this;
        }

    }

}

record UnsignedVector(byte[] binary) implements UVec, UVec.ConVec, RecordConstant {

    @Override
    public int length() {
        return binary.length;
    }

    @Override
    public UnsignedVector constant() {
        return this;
    }

}

class GrowingVector implements UVec {

    private final ArraySegments segments = ArraySegments.instance();
    private int pointer;
    private byte[] buffer;

    public GrowingVector() {
        this.buffer = new byte[16];
    }

    protected void prepare(int length) {
        if (pointer + length >= buffer.length) buffer = Arrays.copyOf(buffer, (pointer + length / 16 + 2) * 16);
    }

    public void write(byte b) {
        if (pointer >= buffer.length) buffer = Arrays.copyOf(buffer, (pointer / 16 + 2) * 16);
        this.buffer[pointer++] = b;
    }

    public void write(short s) {
        this.prepare(2);
        this.segments.putShort(s, buffer, pointer);
        this.pointer += 2;
    }

    public void write(int i) {
        this.prepare(4);
        this.segments.putInt(i, buffer, pointer);
        this.pointer += 4;
    }

    public void write(long j) {
        this.prepare(8);
        this.segments.putLong(j, buffer, pointer);
        this.pointer += 8;
    }

    @Override
    public int length() {
        return pointer;
    }

    @Override
    public byte[] binary() {
        if (pointer == buffer.length) return buffer;
        return Arrays.copyOf(buffer, pointer);
    }

    @Override
    public ConVec constant() {
        return new UnsignedVector(this.binary());
    }

}
