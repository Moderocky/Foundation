package org.valross.foundation.assembler;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * A utility for copying and managing arrays according to their octet length,
 * rather than their unit length.
 * An array's octet length is the number of (8-bit) bytes needed to represent it, even if it is an array of
 * something other than bytes.
 * The octet length is used for copying raw, binary data directly between arrays rather than the actual units of data.
 * The best implementation {@link UnsafeArraySegments} uses Java's Unsafe to perform 'memcpy' directly, however,
 * for environments where Unsafe is unavailable, a slower version {@link SlightlyRubbishArraySegments} is also
 * available.
 */
public abstract class ArraySegments {

    public static ArraySegments segments;

    public static ArraySegments instance() {
        if (segments == null) {
            try {
                final Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                final Unsafe unsafe = (Unsafe) field.get(null);
                segments = new UnsafeArraySegments(unsafe);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                segments = new SlightlyRubbishArraySegments();
            }

        }
        return segments;
    }

    public abstract void arrayCopy(Object source, Object target);

    public abstract void arrayCopy(Object source, Object target, int length);

    protected int octetLength(Object array) {
        return switch (array) {
            case byte[] data -> data.length;
            case short[] data -> data.length * Short.BYTES;
            case char[] data -> data.length * Character.BYTES;
            case int[] data -> data.length * Integer.BYTES;
            case long[] data -> data.length * Long.BYTES;
            default -> throw new IllegalStateException("Unexpected array: " + array);
        };
    }

    protected int typeLength(Class<?> component) {
        if (component == byte.class) return 1;
        if (component == short.class) return 2;
        if (component == char.class) return 2;
        if (component == int.class) return 4;
        if (component == long.class) return 8;
        return -1;
    }

    public void putChar(char value, byte[] bytes, int offset) {
        final byte[] buffer = new byte[2];
        buffer[0] = (byte) (value >>> 8);
        buffer[1] = (byte) (value);
        this.put(buffer, bytes, offset);
    }

    public void putShort(short value, byte[] bytes, int offset) {
        final byte[] buffer = new byte[2];
        buffer[0] = (byte) (value >>> 8);
        buffer[1] = (byte) (value);
        this.put(buffer, bytes, offset);
    }

    public void putInt(int value, byte[] bytes, int offset) {
        final byte[] buffer = new byte[4];
        buffer[0] = (byte) (value >>> 24);
        buffer[1] = (byte) (value >>> 16);
        buffer[2] = (byte) (value >>> 8);
        buffer[3] = (byte) (value);
        this.put(buffer, bytes, offset);
    }

    public void putLong(long value, byte[] bytes, int offset) {
        final byte[] buffer = new byte[8];
        buffer[0] = (byte) (value >>> 56);
        buffer[1] = (byte) (value >>> 48);
        buffer[2] = (byte) (value >>> 40);
        buffer[3] = (byte) (value >>> 32);
        buffer[4] = (byte) (value >>> 24);
        buffer[5] = (byte) (value >>> 16);
        buffer[6] = (byte) (value >>> 8);
        buffer[7] = (byte) (value);
        this.put(buffer, bytes, offset);
    }

    private void put(byte[] buffer, byte[] bytes, int offset) {
        System.arraycopy(buffer, 0, bytes, offset, Math.min(buffer.length, bytes.length - offset));
    }

    public char getChar(byte[] bytes, int offset) {
        return (char) ((read(bytes, offset) << 8) + read(bytes, offset + 1));
    }

    public short getShort(byte[] bytes, int offset) {
        return (short) ((read(bytes, offset) << 8) + read(bytes, offset + 1));
    }

    public int getInt(byte[] bytes, int offset) {
        return ((read(bytes, offset) << 24) + (read(bytes, offset + 1) << 16) + (read(bytes, offset + 2) << 8) + read(bytes, offset + 3));
    }

    public long getLong(byte[] bytes, int offset) {
        return ((long) read(bytes, offset++) << 56) + (((long) read(bytes, offset++) & 255) << 48) + (((long) read(bytes, offset++) & 255) << 40) + (((long) read(bytes, offset++) & 255) << 32) + (((long) read(bytes, offset++) & 255) << 24) + (((long) read(bytes, offset++) & 255) << 16) + (((long) read(bytes, offset++) & 255) << 8) + ((long) read(bytes, offset) & 255);
    }

    private byte read(byte[] bytes, int position) {
        return position < bytes.length ? bytes[position] : 0;
    }

}

class SlightlyRubbishArraySegments extends ArraySegments {

    @Override
    public void arrayCopy(Object source, Object target) {
        final int length = Math.min(octetLength(source), octetLength(target));
        arrayCopy(source, target, length);
    }

    @Override
    public void arrayCopy(Object source, Object target, int length) {
        if (source.getClass() == target.getClass()) {
            copySameType(source, target, length);
            return;
        }
        final byte[] data;
        if (source instanceof byte[] bytes) data = bytes;
        else {
            data = new byte[length];
            this.copyBytes(source, data, length);
        }
        this.copyBytes(data, target, length);
    }

    private void copyBytes(Object source, byte[] target, int length) {
        if (source instanceof byte[]) {
            System.arraycopy(source, 0, target, 0, length);
            return;
        }
        final int scale = this.typeLength(source.getClass().getComponentType());
        final int adjusted = length / scale;
        switch (source) {
            case short[] array:
                for (int i = 0; i < adjusted; i++) this.putShort(array[i], target, i * scale);
                break;
            case char[] array:
                for (int i = 0; i < adjusted; i++) this.putChar(array[i], target, i * scale);
                break;
            case int[] array:
                for (int i = 0; i < adjusted; i++) this.putInt(array[i], target, i * scale);
                break;
            case long[] array:
                for (int i = 0; i < adjusted; i++) this.putLong(array[i], target, i * scale);
                break;
            default:
                throw new IllegalStateException("Unexpected array: " + source);
        }
    }

    private void copyBytes(byte[] bytes, Object target, int length) {
        if (target instanceof byte[]) {
            System.arraycopy(bytes, 0, target, 0, length);
            return;
        }
        final int scale = this.typeLength(target.getClass().getComponentType());
        final int adjusted = length / scale;
        switch (target) {
            case short[] array:
                for (int i = 0; i < adjusted; i++) array[i] = this.getShort(bytes, i * scale);
                break;
            case char[] array:
                for (int i = 0; i < adjusted; i++) array[i] = this.getChar(bytes, i * scale);
                break;
            case int[] array:
                for (int i = 0; i < adjusted; i++) array[i] = this.getInt(bytes, i * scale);
                break;
            case long[] array:
                for (int i = 0; i < adjusted; i++) array[i] = this.getLong(bytes, i * scale);
                break;
            default:
                throw new IllegalStateException("Unexpected array: " + target);
        }
    }

    private void copySameType(Object source, Object target, int length) {
        assert source.getClass().isArray() && target.getClass().isArray();
        final int adjusted = length / this.typeLength(source.getClass().getComponentType());
        System.arraycopy(source, 0, target, 0, adjusted);
    }

}

class UnsafeArraySegments extends ArraySegments {

    private final Unsafe unsafe;

    UnsafeArraySegments(Unsafe unsafe) {
        this.unsafe = unsafe;
    }

    @Override
    public void arrayCopy(Object source, Object target) {
        final int length = Math.min(octetLength(source), octetLength(target));
        arrayCopy(source, target, length);
    }

    @Override
    public void arrayCopy(Object source, Object target, int length) {
        final int us = unsafe.arrayBaseOffset(source.getClass());
        final int them = unsafe.arrayBaseOffset(target.getClass());
        unsafe.copyMemory(source, us, target, them, length);
    }

}
