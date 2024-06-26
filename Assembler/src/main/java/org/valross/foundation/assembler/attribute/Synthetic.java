package org.valross.foundation.assembler.attribute;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.UVec;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public record Synthetic(
    PoolReference attribute_name_index)
    implements AttributeInfo.FieldAttribute, ZeroAttribute, AttributeInfo.CodeAttribute, AttributeInfo, UVec,
    RecordConstant {

    public Synthetic(ClassFileBuilder.Storage storage) {
        this(storage.constant(UTF8, "Synthetic"));
    }

}
