package mx.kenzie.foundation.assembler.attribute.frame;

import mx.kenzie.foundation.assembler.vector.U1;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

public record ShiftFrame(int offset, int amount) implements StackMapFrame, RecordConstant {

    @Override
    public U1 frame_type() {
        assert amount > -4 && amount < 4 && amount != 0;
        return U1.valueOf(251 + amount);
    }

    @Override
    public UVec info() {
        return this.offset_delta();
    }

    public U2 offset_delta() {
        return U2.valueOf(offset);
    }

}
