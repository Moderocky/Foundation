package org.valross.foundation.assembler.constant;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.foundation.detail.Type;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record ClassInfo(PoolReference name_index) implements ConstantPoolInfo, Data, UVec, RecordConstant {

    @Override
    public ConstantType<ConstantPoolInfo, Type> tag() {
        return ConstantPoolInfo.TYPE;
    }

    @Override
    public UVec info() {
        return name_index;
    }

    @Override
    public boolean is(Constable object) {
        return object instanceof Type type && name_index.ensure().is(type.internalName())
            || object instanceof java.lang.reflect.Type other && name_index.ensure().is(Type.internalName(other));
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        ConstantPoolInfo.super.write(stream);
    }

    @Override
    public int sort() {
        return 11;
    }

    @Override
    public Type unpack() {
        return Type.fromInternalName(ConstantPoolInfo.UTF8.unpack(name_index.get()));
    }

    @Override
    public int length() {
        return 3;
    }

}
