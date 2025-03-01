package org.valross.foundation.assembler.constant;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.foundation.detail.Member;

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
        return object instanceof Member member
            && class_index.ensure().is(member.owner())
            && name_and_type_index.ensure().is(member.signature());
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        this.tag().write(stream);
        this.class_index().write(stream);
        this.name_and_type_index().write(stream);
    }

    @Override
    public int sort() {
        return 40;
    }

    @Override
    public Member unpack() {
        return new Member(ConstantPoolInfo.TYPE.unpack(class_index.get()),
                          ConstantPoolInfo.NAME_AND_TYPE.unpack(name_and_type_index.get()));
    }

    @Override
    public int length() {
        return 5;
    }

}
