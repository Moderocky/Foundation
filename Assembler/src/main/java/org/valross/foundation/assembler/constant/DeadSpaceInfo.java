package org.valross.foundation.assembler.constant;

import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

/**
 * This class is a complete waste of space. It's simply there to occupy space in the pool array.
 * The creators of the class file format, in their heavenly wisdom, decided that
 * longs and doubles ought to take up TWO indices in the constant pool.
 * The first index has 1 byte for the type tag, 4 bytes for the first half and 4 bytes for the second half,
 * for a total of 9 bytes.
 * The second index has 0 bytes, and contains nothing. It is effectively skipped.
 * Mere mortals like ourselves might not see a reason for this,
 * since there is absolutely nothing wrong with using a single 9-byte space.
 * However, we are utter fools! We simply cannot see the great wisdom of skipping the next index.
 * Compared to the masterful class file spec designers, we are fumbling in the dark.
 * One day, I pray that we can all reach the transcendent level when the reasons for
 * such a profound design become clear to us.
 */
public record DeadSpaceInfo() implements ConstantPoolInfo {

    @Override
    public ConstantType<?, ?> tag() {
        return null;
    }

    @Override
    public UVec info() {
        return UVec.of();
    }

    @Override
    public boolean is(Constable object) {
        return false;
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
    }

    @Override
    public byte[] binary() {
        return new byte[0];
    }

    @Override
    public int sort() {
        return -1;
    }

    @Override
    public int length() {
        return 0;
    }

}
