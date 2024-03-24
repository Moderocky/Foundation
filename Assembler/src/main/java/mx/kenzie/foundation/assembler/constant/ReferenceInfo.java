package mx.kenzie.foundation.assembler.constant;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.assembler.vector.UVec;
import mx.kenzie.foundation.detail.Member;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record ReferenceInfo(ConstantType<ReferenceInfo, Member> tag, PoolReference class_index,
                            PoolReference name_and_type_index)
    implements ConstantPoolInfo, Data, UVec, RecordConstant {

    @Override
    public UVec info() {
        return UVec.of(class_index(), name_and_type_index());
    }

    @Override
    public boolean is(Constable object) {
        return object instanceof Member member && class_index.ensure().is(member.owner())
            && name_and_type_index.ensure().is(member.signature());
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.tag().write(stream);
        this.class_index().write(stream);
        this.name_and_type_index().write(stream);
    }

    @Override
    public int sort() {
        return 40;
    }

    @Override
    public int length() {
        return 5;
    }

}
