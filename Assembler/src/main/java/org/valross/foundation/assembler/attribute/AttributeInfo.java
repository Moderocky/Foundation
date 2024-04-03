package org.valross.foundation.assembler.attribute;

import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.tool.AttributeBuilder;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public interface AttributeInfo extends Data, UVec, AttributeBuilder {

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

    default @Override AttributeInfo build() {
        return this;
    }

    default @Override void finalise() {
    }

    default void debug(String indent, PrintStream stream) {
        stream.print(indent);
        stream.println(this);
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

    /**
     * An attribute that can be placed on a class file.
     * Note that this is for ANYTHING that can be a class attribute (even if it can also be a field attribute, etc.)
     * and is used simply to prevent non-class attributes from being accidentally listed.
     */
    interface TypeAttribute extends AttributeInfo {

    }

}

