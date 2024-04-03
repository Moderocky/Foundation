package mx.kenzie.foundation.instruction;

import static mx.kenzie.foundation.assembler.code.OpCode.*;

public class Return extends MultiTypeInstruction {

    Return() {
    }

    @Override
    public ReturnInstruction object(Instruction.Input<Object> instruction) {
        return builder -> {
            instruction.write(builder);
            builder.write(ARETURN);
        };
    }

    @Override
    public ReturnInstruction intValue(Instruction.Input<Integer> instruction) {
        return builder -> {
            instruction.write(builder);
            builder.write(IRETURN);
        };
    }

    @Override
    public ReturnInstruction longValue(Instruction.Input<Long> instruction) {
        return builder -> {
            instruction.write(builder);
            builder.write(LRETURN);
        };
    }

    @Override
    public ReturnInstruction floatValue(Instruction.Input<Float> instruction) {
        return builder -> {
            instruction.write(builder);
            builder.write(FRETURN);
        };
    }

    @Override
    public ReturnInstruction doubleValue(Instruction.Input<Double> instruction) {
        return builder -> {
            instruction.write(builder);
            builder.write(DRETURN);
        };
    }

    @Override
    public ReturnInstruction none() {
        return builder -> builder.write(RETURN);
    }

    public interface ReturnInstruction extends Instruction.Base {}

}
