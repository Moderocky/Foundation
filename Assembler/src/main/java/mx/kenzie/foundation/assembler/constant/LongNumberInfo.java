package mx.kenzie.foundation.assembler.constant;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.vector.U4;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

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
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.tag.write(stream);
        this.high_bytes.write(stream);
        this.low_bytes.write(stream);
    }

    @Override
    public int sort() {
        return 5;
    }

    @Override
    public int length() {
        return 9;
    }

}
