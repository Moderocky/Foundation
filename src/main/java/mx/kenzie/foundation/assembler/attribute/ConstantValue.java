package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.AttributeInfo;
import mx.kenzie.foundation.assembler.U2;
import mx.kenzie.foundation.assembler.U4;
import mx.kenzie.foundation.assembler.UVec;
import org.valross.constantine.RecordConstant;

public record ConstantValue(U2 attribute_name_index, U2 constantvalue_index)
    implements AttributeInfo, UVec, RecordConstant {

    @Override
    public U4 attribute_length() {
        return U4.valueOf(4);
    }

    @Override
    public UVec info() {
        return constantvalue_index;
    }

}
