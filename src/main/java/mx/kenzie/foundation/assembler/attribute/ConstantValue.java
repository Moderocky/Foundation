package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.U4;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

public record ConstantValue(PoolReference attribute_name_index, PoolReference constantvalue_index)
    implements AttributeInfo.FieldAttribute, AttributeInfo, UVec, RecordConstant {

    public static final String ATTRIBUTE_NAME = ConstantValue.class.getSimpleName();

    @Override
    public U4 attribute_length() {
        return U4.valueOf(2);
    }

    @Override
    public UVec info() {
        return constantvalue_index;
    }

    @Override
    public String attributeName() {
        return ATTRIBUTE_NAME;
    }

}
