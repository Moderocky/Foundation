package org.valross.foundation.assembler.constant;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record LongNumberInfo<Type extends Number & Constable>(ConstantType<LongNumberInfo<Type>, Type> tag,
                                                              U4 high_bytes, U4 low_bytes)
    implements ConstantPoolInfo, Data, UVec, RecordConstant {

    @Override
    public UVec info() {
        return UVec.of(high_bytes, low_bytes);
    }

    @Override
    public boolean is(Constable object) {
        if (tag == (Object) LONG)
            return object instanceof Number number && number.longValue() == high_bytes.longValue(low_bytes);
        if (tag == (Object) DOUBLE)
            return object instanceof Number number && number.doubleValue() == high_bytes.doubleValue(low_bytes);
        return false;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        this.tag.write(stream);
        this.high_bytes.write(stream);
        this.low_bytes.write(stream);
    }

    @Override
    public int sort() {
        return 5;
    }

    @Override
    public Number unpack() {
        if (tag == (Object) LONG)
            return high_bytes.longValue(low_bytes);
        return high_bytes.doubleValue(low_bytes);
    }

    @Override
    public int length() {
        return 9;
    }

}
