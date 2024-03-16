package mx.kenzie.foundation.assembler;

import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;

import java.io.IOException;
import java.io.OutputStream;

import static mx.kenzie.foundation.assembler.ConstantPoolInfo.UTF8;

public interface AttributeInfo
    extends Data, UVec {

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

    static PoolReference name(AttributeInfo info, ClassFileBuilder.Helper helper) {
        return helper.constant(UTF8, info.attributeName());
    }

}

