package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.U4;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;

import java.io.IOException;
import java.io.OutputStream;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public interface AttributeInfo
    extends Data, UVec {

    static PoolReference name(AttributeInfo info, ClassFileBuilder.Storage storage) {
        return storage.constant(UTF8, info.attributeName());
    }

    UVec attribute_name_index();

    U4 attribute_length();

    UVec info();

    @Override
    default int length() {
        return (int) (6L + this.attribute_length().value());
    }

    @Override
    default void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.attribute_name_index().write(stream);
        this.attribute_length().write(stream);
        this.info().write(stream);
    }

    default String attributeName() {
        return this.getClass().getSimpleName();
    }

    /**
     * An attribute that can be placed on a code block.
     * Note that this is for ANYTHING that can be a code attribute (even if it can also be a file attribute, etc.)
     * and is used simply to prevent non-code attributes from being accidentally listed.
     */
    interface CodeAttribute extends AttributeInfo {

    }

    /**
     * An attribute that can be placed on a field.
     * Note that this is for ANYTHING that can be a field attribute (even if it can also be a file attribute, etc.)
     * and is used simply to prevent non-field attributes from being accidentally listed.
     */
    interface FieldAttribute extends AttributeInfo {

    }

}

