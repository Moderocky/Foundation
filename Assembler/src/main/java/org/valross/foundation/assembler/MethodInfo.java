package org.valross.foundation.assembler;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.attribute.AttributeInfo;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U2;

import java.io.IOException;
import java.io.OutputStream;

public record MethodInfo(U2 access_flags, PoolReference name_index, PoolReference descriptor_index,
                         AttributeInfo... attributes) implements Data, RecordConstant {

    public U2 attributes_count() {
        return U2.valueOf(attributes.length);
    }

    @Override
    public int length() {
        int length = 8;
        for (AttributeInfo info : attributes) length += info.length();
        return length;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        this.access_flags.write(stream);
        this.name_index.write(stream);
        this.descriptor_index.write(stream);
        this.attributes_count().write(stream);
        for (AttributeInfo attribute : attributes) attribute.write(stream);
    }

}
