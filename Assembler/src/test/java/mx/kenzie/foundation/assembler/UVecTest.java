package mx.kenzie.foundation.assembler;

import org.junit.Test;

import java.util.Arrays;

public class UVecTest {

    @Test
    public void store() {
        final ArraySegments segments = ArraySegments.instance();
        final byte zero = 0X0;
        final byte[] source, target;
        source = new byte[] {1, 7, 10, 14, -4, 8, 5, 2};
        target = new byte[source.length];
        final int[] ints = new int[2];
        segments.arrayCopy(source, ints);
        segments.arrayCopy(ints, target);
        assert Arrays.equals(source, target);

        Arrays.fill(target, zero);
        final short[] shorts = new short[4];
        segments.arrayCopy(source, shorts);
        segments.arrayCopy(shorts, target);
        assert Arrays.equals(source, target);

        Arrays.fill(target, zero);
        final short[] whoops = new short[10];
        segments.arrayCopy(source, whoops);
        segments.arrayCopy(whoops, target);
        assert Arrays.equals(source, target);

        Arrays.fill(target, zero);
        segments.arrayCopy(source, target);
        assert Arrays.equals(source, target);
    }

    @Test
    public void storeRubbish() {
        final ArraySegments segments = new SlightlyRubbishArraySegments();
        final byte zero = 0X0;
        final byte[] source, target;
        source = new byte[] {1, 7, 10, 14, -4, 8, 5, 2};
        target = new byte[source.length];
        final int[] ints = new int[2];
        segments.arrayCopy(source, ints);
        segments.arrayCopy(ints, target);
        assert Arrays.equals(source, target) : "\n" + Arrays.toString(source) + "\n" + Arrays.toString(target);

        Arrays.fill(target, zero);
        final short[] shorts = new short[4];
        segments.arrayCopy(source, shorts);
        segments.arrayCopy(shorts, target);
        assert Arrays.equals(source, target) : "\n" + Arrays.toString(source) + "\n" + Arrays.toString(target);

        Arrays.fill(target, zero);
        final short[] whoops = new short[10];
        segments.arrayCopy(source, whoops);
        segments.arrayCopy(whoops, target);
        assert Arrays.equals(source, target) : "\n" + Arrays.toString(source) + "\n" + Arrays.toString(target);

        Arrays.fill(target, zero);
        segments.arrayCopy(source, target);
        assert Arrays.equals(source, target) : "\n" + Arrays.toString(source) + "\n" + Arrays.toString(target);
    }

}