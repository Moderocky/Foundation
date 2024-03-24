package mx.kenzie.foundation.assembler.constant;

import mx.kenzie.foundation.Member;
import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.U1;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record MethodHandleInfo(ConstantType<MethodHandleInfo, Member.Invocation> tag, Member.Invocation invocation,
                               U1 reference_kind,
                               PoolReference reference_index)
    implements ConstantPoolInfo, Data, UVec, RecordConstant {

    @Override
    public UVec info() {
        return UVec.of(reference_kind, reference_index);
    }

    @Override
    public boolean is(Constable object) {
        return invocation.equals(object);
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        ConstantPoolInfo.super.write(stream);
    }

    @Override
    public int sort() {
        return 55;
    }

    @Override
    public int length() {
        return 4;
    }

}
