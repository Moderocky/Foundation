package org.valross.foundation.assembler.attribute;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;

public record LocalVariableTable(PoolReference attribute_name_index, U2 local_variable_table_length,
                                 LocalVariable... local_variable_table)
    implements AttributeInfo.CodeAttribute, AttributeInfo, UVec, RecordConstant {

    @Override
    public U4 attribute_length() {
        return U4.valueOf(2 + local_variable_table.length * 10);
    }

    @Override
    public UVec info() {
        return UVec.of(local_variable_table_length, UVec.of(local_variable_table));
    }

    public record LocalVariable(U2 start_pc, U2 code_length, // length
                                PoolReference name_index, PoolReference descriptor_index,
                                U2 index) implements UVec, RecordConstant {

        @Override
        public int length() {
            return 10;
        }

        @Override
        public void write(OutputStream stream) throws IOException {
            this.start_pc.write(stream);
            this.code_length.write(stream);
            this.name_index.write(stream);
            this.descriptor_index.write(stream);
            this.index.write(stream);
        }

    }

}
