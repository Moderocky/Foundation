package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.U4;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.AttributeBuilder;
import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

import java.lang.constant.Constable;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.UTF8;

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
