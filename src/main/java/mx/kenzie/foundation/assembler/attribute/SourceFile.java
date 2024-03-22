package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.U4;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

public record SourceFile(PoolReference attribute_name_index, PoolReference sourcefile_index)
    implements AttributeInfo.TypeAttribute, AttributeInfo, UVec, RecordConstant {

    @Override
    public U4 attribute_length() {
        return U4.valueOf(2);
    }

    @Override
    public UVec info() {
        return sourcefile_index;
    }

}
