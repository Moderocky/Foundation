package org.valross.foundation.assembler.attribute;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.tool.AttributeBuilder;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;

import java.lang.constant.Constable;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public record ConstantValue(PoolReference attribute_name_index, PoolReference constantvalue_index)
    implements AttributeInfo.FieldAttribute, AttributeBuilder, AttributeInfo, UVec, RecordConstant {

    public static final String ATTRIBUTE_NAME = ConstantValue.class.getSimpleName();

    public ConstantValue(ClassFileBuilder.Storage storage, Constable value) {
        this(storage.constant(UTF8, "ConstantValue"), storage.constant(value));
    }

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

    @Override
    public AttributeInfo build() {
        return this;
    }

    @Override
    public void finalise() {
    }

}
