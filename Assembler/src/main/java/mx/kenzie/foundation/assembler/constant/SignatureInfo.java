package mx.kenzie.foundation.assembler.constant;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.assembler.vector.UVec;
import mx.kenzie.foundation.detail.Signature;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record SignatureInfo(PoolReference name_index,
                            PoolReference descriptor_index) implements ConstantPoolInfo, Data, UVec, RecordConstant {

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
        return object instanceof Signature signature
            && name_index.ensure().is(signature.name())
            && descriptor_index.ensure().is(signature.descriptorString());
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
