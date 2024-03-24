package mx.kenzie.foundation.assembler.constant;

import mx.kenzie.foundation.detail.Type;
import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record ClassInfo(PoolReference name_index) implements ConstantPoolInfo, Data, UVec, RecordConstant {

    @Override
    public ConstantType<ClassInfo, Type> tag() {
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
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        ConstantPoolInfo.super.write(stream);
    }

    @Override
    public int sort() {
        return 11;
    }

    @Override
    public int length() {
        return 3;
    }

}
