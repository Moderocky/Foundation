package org.valross.foundation.assembler;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.attribute.AttributeInfo;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U2;

import java.io.IOException;
import java.io.OutputStream;

public record FieldInfo(U2 access_flags, PoolReference name_index, PoolReference descriptor_index, U2 attributes_count,
                        AttributeInfo[] attributes) implements Data, RecordConstant {

    @Override
    public int length() {
        int length = 8;
        for (AttributeInfo attribute : attributes) length += attribute.length();
        return length;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        this.access_flags.write(stream);
        this.name_index.write(stream);
        this.descriptor_index.write(stream);
        this.attributes_count.write(stream);
        for (AttributeInfo info : attributes) info.write(stream);
    }

}
