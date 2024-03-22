package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.U4;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.AttributeBuilder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A stub attribute that has no data except its name index and its length (0)
 * such as deprecation or synthetic notices.
 */
public interface ZeroAttribute extends AttributeInfo, AttributeBuilder {

    @Override
    default U4 attribute_length() {
        return U4.ZERO;
    }

    @Override
    default UVec info() {
        return U4.ZERO;
    }

    @Override
    default int length() {
        return 6;
    }

    @Override
    default void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.attribute_name_index().write(stream);
        this.attribute_length().write(stream);
    }

    default @Override
    AttributeInfo build() {
        return this;
    }

    default @Override
    void finalise() {
    }

}
