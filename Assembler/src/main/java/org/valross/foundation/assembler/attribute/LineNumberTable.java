package org.valross.foundation.assembler.attribute;

import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

public record LineNumberTable(PoolReference attribute_name_index, U2 line_number_table_length,
                              LineNumber... line_number_table)
    implements AttributeInfo.CodeAttribute, AttributeInfo, UVec, RecordConstant {

    @Override
    public U4 attribute_length() {
        return U4.valueOf(2 + line_number_table.length * 4);
    }

    @Override
    public UVec info() {
        return UVec.of(line_number_table_length, UVec.of(line_number_table));
    }

    public record LineNumber(U2 start_pc, U2 line_number) implements Data, UVec, RecordConstant {

        @Override
        public int length() {
            return 4;
        }

        @Override
        public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
            this.start_pc.write(stream);
            this.line_number.write(stream);
        }

    }

}
