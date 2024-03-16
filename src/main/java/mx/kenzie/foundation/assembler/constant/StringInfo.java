package mx.kenzie.foundation.assembler.constant;

import mx.kenzie.foundation.assembler.ConstantPoolInfo;
import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

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
    public int length() {
        return 3;
    }

}
