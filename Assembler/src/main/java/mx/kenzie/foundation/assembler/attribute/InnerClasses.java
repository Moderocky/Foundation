package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.U4;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

public record InnerClasses(PoolReference attribute_name_index, U4 attribute_length, Classes[] classes)
    implements AttributeInfo, UVec, RecordConstant {

    public U2 number_of_classes() {
        return U2.valueOf(classes.length);
    }

    @Override
    public U4 attribute_length() {
        return U4.valueOf(8 + classes.length * 2);
    }

    @Override
    public UVec info() {
        return UVec.of(number_of_classes(), UVec.of(classes));
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.attribute_name_index.write(stream);
        this.attribute_length.write(stream);
        this.number_of_classes().write(stream);
        for (Data reference : classes) reference.write(stream);
    }

    public record Classes(PoolReference inner_class_info_index, PoolReference outer_class_info_index,
                          PoolReference inner_name_index, U2 inner_class_access_flags)
        implements Data, UVec, RecordConstant {

        @Override
        public int length() {
            return 8;
        }

        @Override
        public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
            this.inner_class_info_index.write(stream);
            this.outer_class_info_index.write(stream);
            this.inner_name_index.write(stream);
            this.inner_class_access_flags.write(stream);
        }

    }

}
