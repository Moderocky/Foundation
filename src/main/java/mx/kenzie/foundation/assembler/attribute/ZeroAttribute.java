package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.U4;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.AttributeBuilder;

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

    default @Override
    AttributeInfo build() {
        return this;
    }

    default @Override
    void finalise() {
    }

}
