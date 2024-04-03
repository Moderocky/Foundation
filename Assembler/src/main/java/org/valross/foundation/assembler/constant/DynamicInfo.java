package org.valross.foundation.assembler.constant;

import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.tool.BootstrapReference;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.foundation.detail.DynamicReference;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record DynamicInfo(ConstantType<DynamicInfo, ?> tag, BootstrapReference bootstrap_method_attr_index,
                          PoolReference name_and_type_index) implements ConstantPoolInfo, Data, UVec, RecordConstant {

    @Override
    public UVec info() {
        return UVec.of(bootstrap_method_attr_index, name_and_type_index);
    }

    @Override
    public boolean is(Constable object) {
        return object instanceof DynamicReference reference
            && reference.type().ordinal() + 17 == tag().value().intValue()
            && name_and_type_index.ensure().is(reference.signature()) // we should check arguments properly
            && bootstrap_method_attr_index.ensure().bootstrap_arguments().length == reference.arguments().length // todo
            && bootstrap_method_attr_index.ensure().bootstrap_method_ref().ensure().is(reference.invocation());
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.tag().write(stream);
        this.bootstrap_method_attr_index.write(stream);
        this.name_and_type_index.write(stream);
    }

    @Override
    public int sort() {
        if (tag == ConstantPoolInfo.DYNAMIC) return 61;
        return 62;
    }

    @Override
    public int length() {
        return 5;
    }

}
