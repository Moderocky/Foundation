package org.valross.foundation.assembler.attribute;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;

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
