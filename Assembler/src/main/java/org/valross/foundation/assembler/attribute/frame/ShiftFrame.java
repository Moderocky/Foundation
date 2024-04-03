package org.valross.foundation.assembler.attribute.frame;

import org.valross.foundation.assembler.vector.U1;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

public record ShiftFrame(int offset, int amount, VerificationTypeInfo... added)
    implements StackMapFrame, RecordConstant {

    @Override
    public U1 frame_type() {
        assert amount > -4 && amount < 4 && amount != 0;
        return U1.valueOf(251 + amount);
    }

    @Override
    public UVec info() {
        if (amount < 0)
            return this.offset_delta();
        else return UVec.of(this.offset_delta(), UVec.of(added));
    }

    public U2 offset_delta() {
        return U2.valueOf(offset);
    }

}
