package mx.kenzie.foundation.assembler.constant;

import mx.kenzie.foundation.Descriptor;
import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

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
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        ConstantPoolInfo.super.write(stream);
    }

    @Override
    public int sort() {
        return 28;
    }

    @Override
    public int length() {
        return 3;
    }

}
