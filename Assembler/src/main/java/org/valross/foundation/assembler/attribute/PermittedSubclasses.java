package org.valross.foundation.assembler.attribute;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.tool.AttributeBuilder;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public record PermittedSubclasses(PoolReference attribute_name_index, PoolReference... classes)
    implements AttributeInfo.TypeAttribute, AttributeBuilder, AttributeInfo, UVec, RecordConstant {

    public PermittedSubclasses(ClassFileBuilder.Storage storage, PoolReference... classes) {
        this(storage.constant(UTF8, "PermittedSubclasses"), classes);
    }

    public U2 number_of_classes() {
        return U2.valueOf(classes.length);
    }

    @Override
    public U4 attribute_length() {
        return U4.valueOf(2 + (classes.length * 2));
    }

    @Override
    public UVec info() {
        return UVec.of(number_of_classes(), UVec.of(classes));
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        this.attribute_name_index.write(stream);
        this.attribute_length().write(stream);
        this.number_of_classes().write(stream);
        for (PoolReference reference : classes) reference.write(stream);
    }

}
