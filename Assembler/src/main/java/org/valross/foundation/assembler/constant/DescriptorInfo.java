package org.valross.foundation.assembler.constant;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.foundation.detail.Descriptor;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record DescriptorInfo(PoolReference descriptor_index) implements ConstantPoolInfo, Data, UVec, RecordConstant {

    @Override
    public ConstantType<DescriptorInfo, Descriptor> tag() {
        return ConstantPoolInfo.METHOD_TYPE;
    }

    @Override
    public UVec info() {
        return descriptor_index;
    }

    @Override
    public boolean is(Constable object) {
        return object instanceof Descriptor descriptor && descriptor_index.ensure().is(descriptor.descriptorString());
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        ConstantPoolInfo.super.write(stream);
    }

    @Override
    public int sort() {
        return 28;
    }

    @Override
    public Descriptor unpack() {
        return Descriptor.of(ConstantPoolInfo.UTF8.unpack(descriptor_index.get()));
    }

    @Override
    public int length() {
        return 3;
    }

}
