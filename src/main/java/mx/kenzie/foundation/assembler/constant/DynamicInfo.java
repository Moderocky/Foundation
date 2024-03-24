package mx.kenzie.foundation.assembler.constant;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.BootstrapReference;
import mx.kenzie.foundation.assembler.tool.PoolReference;
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
        // todo
        return false;
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        ConstantPoolInfo.super.write(stream);
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
