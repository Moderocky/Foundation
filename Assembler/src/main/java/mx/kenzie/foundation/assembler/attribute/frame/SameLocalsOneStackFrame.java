package mx.kenzie.foundation.assembler.attribute.frame;

import mx.kenzie.foundation.assembler.vector.U1;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

public record SameLocalsOneStackFrame(int offset, VerificationTypeInfo stack) implements StackMapFrame, RecordConstant {

    private static final U1 TYPE = U1.valueOf(247);

    @Override
    public U1 frame_type() {
        if (offset > 63) return TYPE;
        return U1.valueOf(offset + 64);
    }

    @Override
    public UVec info() {
        if (offset > 63) return UVec.of(this.offset_delta(), stack);
        return UVec.of(stack);
    }

    public U2 offset_delta() {
        return U2.valueOf(offset);
    }

}
