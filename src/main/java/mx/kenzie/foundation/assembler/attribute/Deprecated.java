package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public record Deprecated(PoolReference attribute_name_index)
    implements AttributeInfo.FieldAttribute, AttributeInfo.TypeAttribute, ZeroAttribute, AttributeInfo.CodeAttribute,
    AttributeInfo, UVec,
    RecordConstant {

    public Deprecated(ClassFileBuilder.Storage storage) {
        this(storage.constant(UTF8, "Deprecated"));
    }

}
