package org.valross.foundation.assembler.attribute;

import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

public record Exceptions(PoolReference attribute_name_index,
                         U4 attribute_length,
                         PoolReference[] exception_index_table)
    implements AttributeInfo.CodeAttribute, AttributeInfo, UVec, RecordConstant {

    @Override
    public U4 attribute_length() {
        return U4.valueOf(8 + exception_index_table.length * 2);
    }

    @Override
    public UVec info() {
        return UVec.of(number_of_exceptions(), UVec.of(exception_index_table));
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.attribute_name_index.write(stream);
        this.attribute_length.write(stream);
        this.number_of_exceptions().write(stream);
        for (PoolReference reference : exception_index_table) reference.write(stream);
    }

    public U2 number_of_exceptions() {
        return U2.valueOf(exception_index_table.length);
    }

}
