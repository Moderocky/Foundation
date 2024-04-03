package org.valross.foundation.assembler.constant;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record NumberInfo<Type extends Number & Constable>(ConstantType<NumberInfo<Type>, Type> tag, U4 bytes)
    implements ConstantPoolInfo, Data, UVec, RecordConstant {

    @Override
    public UVec info() {
        return bytes;
    }

    @Override
    public boolean is(Constable object) {
        if (tag == (Object) INTEGER)
            return object instanceof Number number && number.intValue() == bytes.intValue();
        if (tag == (Object) FLOAT)
            return object instanceof Number number && number.floatValue() == bytes.floatValue();
        return false;
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.tag.write(stream);
        this.bytes.write(stream);
    }

    @Override
    public int sort() {
        return 4;
    }

    @Override
    public Number unpack() {
        if (tag == (Object) INTEGER)
            return bytes.intValue();
        else return bytes.floatValue();
    }

    @Override
    public int length() {
        return 5;
    }

}
