package mx.kenzie.foundation.assembler.constant;

import mx.kenzie.foundation.Signature;
import mx.kenzie.foundation.assembler.ConstantPoolInfo;
import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record SignatureInfo(PoolReference name_index, PoolReference descriptor_index)
    implements ConstantPoolInfo, Data, UVec, RecordConstant {

    @Override
    public ConstantType<SignatureInfo, Signature> tag() {
        return ConstantPoolInfo.NAME_AND_TYPE;
    }

    @Override
    public UVec info() {
        return UVec.of(name_index, descriptor_index);
    }

    @Override
    public boolean is(Constable object) {
        // TODO
        return false;
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        ConstantPoolInfo.super.write(stream);
    }

    @Override
    public int sort() {
        return 30;
    }

    @Override
    public int length() {
        return 5;
    }

}
