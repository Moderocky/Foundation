package mx.kenzie.foundation.assembler;

import java.io.IOException;
import java.io.OutputStream;

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

    @Override
    default byte[] binary() { // inefficient but subclasses should deal with this
        return UVec.of(this.attribute_name_index(), this.attribute_length(), this.info()).binary();
    }

}

