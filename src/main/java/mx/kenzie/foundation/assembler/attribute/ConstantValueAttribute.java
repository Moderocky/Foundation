package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.AttributeInfo;
import mx.kenzie.foundation.assembler.U2;
import mx.kenzie.foundation.assembler.U4;
import mx.kenzie.foundation.assembler.UVec;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

public record ConstantValueAttribute(U2 attribute_name_index, U2 constantvalue_index)
    implements AttributeInfo, UVec, RecordConstant {

    @Override
    public U4 attribute_length() {
        return U4.valueOf(4);
    }

    @Override
    public UVec info() {
        return constantvalue_index;
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        AttributeInfo.super.write(stream);
    }

    @Override
    public byte[] binary() {
        return AttributeInfo.super.binary();
    }

}
