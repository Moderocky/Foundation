package org.valross.foundation.assembler.attribute;

import org.valross.foundation.assembler.attribute.frame.StackMapFrame;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public record StackMapTable(PoolReference attribute_name_index, StackMapFrame... entries)
    implements AttributeInfo.CodeAttribute, AttributeInfo, UVec, RecordConstant {

    public StackMapTable(ClassFileBuilder.Storage storage, StackMapFrame... entries) {
        this(storage.constant(UTF8, "StackMapTable"), entries);
    }

    public U2 number_of_entries() {
        return U2.valueOf(entries.length);
    }

    @Override
    public U4 attribute_length() {
        int length = 2;
        for (StackMapFrame entry : entries) length += entry.length();
        return U4.valueOf(length);
    }

    @Override
    public UVec info() {
        return UVec.of(number_of_entries(), UVec.of(entries));
    }

}
