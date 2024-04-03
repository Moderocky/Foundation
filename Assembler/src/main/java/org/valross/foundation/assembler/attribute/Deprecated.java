package org.valross.foundation.assembler.attribute;

import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public record Deprecated(PoolReference attribute_name_index)
    implements AttributeInfo.FieldAttribute, AttributeInfo.TypeAttribute, ZeroAttribute, AttributeInfo.CodeAttribute,
    AttributeInfo, UVec, RecordConstant {

    public Deprecated(ClassFileBuilder.Storage storage) {
        this(storage.constant(UTF8, "Deprecated"));
    }

    @Override
    public int length() {
        return attribute_name_index.length();
    }

}
