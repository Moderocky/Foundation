package org.valross.foundation.assembler.constant;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record StringInfo(PoolReference string_index)
    implements ConstantPoolInfo, Data, UVec, RecordConstant {

    @Override
    public ConstantType<StringInfo, String> tag() {
        return ConstantPoolInfo.STRING;
    }

    @Override
    public UVec info() {
        return string_index;
    }

    @Override
    public boolean is(Constable object) {
        return object instanceof String string && string_index.ensure().is(string);
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        ConstantPoolInfo.super.write(stream);
    }

    @Override
    public int sort() {
        return 10;
    }

    @Override
    public String unpack() {
        return ConstantPoolInfo.UTF8.unpack(string_index.get());
    }

    @Override
    public int length() {
        return 3;
    }

}
