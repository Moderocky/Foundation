package mx.kenzie.foundation.assembler.attribute.frame;

import mx.kenzie.foundation.assembler.vector.U1;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

public record SameFrame(int offset) implements StackMapFrame, RecordConstant {

    private static final U1 TYPE = U1.valueOf(251);

    @Override
    public U1 frame_type() {
        if (offset > 63) return TYPE;
        return U1.valueOf(offset);
    }

    @Override
    public UVec info() {
        if (offset > 63) return this.offset_delta();
        return UVec.of();
    }

    public U2 offset_delta() {
        return U2.valueOf(offset);
    }

}
