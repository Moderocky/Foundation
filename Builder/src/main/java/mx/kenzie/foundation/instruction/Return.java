package mx.kenzie.foundation.instruction;

import static mx.kenzie.foundation.assembler.code.OpCode.*;

public class Return extends MultiTypeInstruction {

    Return() {
    }

    @Override
    public Instruction.Base object(Instruction.Input<Object> instruction) {
        return builder -> {
            instruction.write(builder);
            builder.write(ARETURN);
        };
    }

    @Override
    public Instruction.Base intValue(Instruction.Input<Integer> instruction) {
        return builder -> {
            instruction.write(builder);
            builder.write(IRETURN);
        };
    }

    @Override
    public Instruction.Base longValue(Instruction.Input<Long> instruction) {
        return builder -> {
            instruction.write(builder);
            builder.write(LRETURN);
        };
    }

    @Override
    public Instruction.Base floatValue(Instruction.Input<Float> instruction) {
        return builder -> {
            instruction.write(builder);
            builder.write(FRETURN);
        };
    }

    @Override
    public Instruction.Base doubleValue(Instruction.Input<Double> instruction) {
        return builder -> {
            instruction.write(builder);
            builder.write(DRETURN);
        };
    }

    @Override
    public Instruction.Base none() {
        return builder -> builder.write(RETURN);
    }

}
