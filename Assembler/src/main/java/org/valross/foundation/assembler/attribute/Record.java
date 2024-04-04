package org.valross.foundation.assembler.attribute;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.tool.AttributeBuilder;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.foundation.detail.Signature;

import java.io.IOException;
import java.io.OutputStream;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public record Record(PoolReference attribute_name_index, RecordComponent... record_component_info)
    implements AttributeInfo.TypeAttribute, AttributeBuilder, AttributeInfo, UVec, RecordConstant {

    public Record(ClassFileBuilder.Storage storage, RecordComponent... record_component_info) {
        this(storage.constant(UTF8, "Record"), record_component_info);
    }

    public static Record of(ClassFileBuilder.Storage storage, Signature... components) {
        final RecordComponent[] array = new RecordComponent[components.length];
        for (int i = 0; i < array.length; i++)
            array[i] = new RecordComponent(storage.constant(UTF8, components[i].name()),
                                           storage.constant(UTF8, components[i].returnType().descriptorString()));
        return new Record(storage.constant(UTF8, "Record"), array);
    }

    public U2 components_count() {
        return U2.valueOf(record_component_info.length);
    }

    @Override
    public U4 attribute_length() {
        int length = 2;
        for (RecordComponent component : record_component_info) length += component.length();
        return U4.valueOf(length);
    }

    @Override
    public UVec info() {
        return UVec.of(components_count(), UVec.of(record_component_info));
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.attribute_name_index.write(stream);
        this.attribute_length().write(stream);
        this.components_count().write(stream);
        for (RecordComponent method : record_component_info) method.write(stream);
    }

    public record RecordComponent(PoolReference name_index, PoolReference descriptor_index,
                                  AttributeInfo... attributes)
        implements UVec, Data, RecordConstant {

        public U2 attributes_count() {
            return U2.valueOf(attributes.length);
        }

        @Override
        public int length() {
            int length = 6;
            for (AttributeInfo attribute : attributes) length += attribute.length();
            return length;
        }

        @Override
        public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
            this.name_index.write(stream);
            this.descriptor_index.write(stream);
            this.attributes_count().write(stream);
            for (AttributeInfo attribute : attributes) attribute.write(stream);
        }

    }

}
