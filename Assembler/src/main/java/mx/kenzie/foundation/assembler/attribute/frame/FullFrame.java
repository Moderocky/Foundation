package mx.kenzie.foundation.assembler.attribute.frame;

import mx.kenzie.foundation.assembler.vector.U1;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

import java.util.Arrays;

public record FullFrame(int offset, VerificationTypeInfo[] locals, VerificationTypeInfo... stack)
    implements StackMapFrame, RecordConstant {

    private static final U1 TYPE = U1.valueOf(255);

    @Override
    public U1 frame_type() {
        return TYPE;
    }

    @Override

    public UVec info() {
        if (offset > 63) return U2.valueOf(offset);
        return UVec.of(this.offset_delta(), this.number_of_locals(), UVec.of(locals), this.number_of_stack_items(),
                       UVec.of(stack));
    }

    public U2 offset_delta() {
        return U2.valueOf(offset);
    }

    public U2 number_of_locals() {
        return U2.valueOf(locals.length);
    }

    public U2 number_of_stack_items() {
        return U2.valueOf(stack.length);
    }

    @Override
    public String toString() {
        return "FullFrame[" +
            "offset_delta=" + offset +
            ", number_of_locals=" + number_of_locals() +
            ", locals=" + Arrays.toString(locals) +
            ", number_of_stack_items=" + number_of_stack_items() +
            ", stack=" + Arrays.toString(stack) +
            ']';
    }

}
