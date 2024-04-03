package mx.kenzie.foundation.instruction;

import static org.valross.foundation.assembler.code.OpCode.*;

public class StoreVariable {

    StoreVariable() {
    }

    public Instruction.Base object(int index, Instruction.Input<?> value) {
        return builder -> {
            value.write(builder);
            builder.write(ASTORE.var(index));
        };
    }

    public Instruction.Base byteValue(int index, Instruction.Input<Integer> value) {
        return this.intValue(index, value);
    }

    public Instruction.Base intValue(int index, Instruction.Input<Integer> value) {
        return builder -> {
            value.write(builder);
            builder.write(ISTORE.var(index));
        };
    }

    public Instruction.Base shortValue(int index, Instruction.Input<Integer> value) {
        return this.intValue(index, value);
    }

    public Instruction.Base longValue(int index, Instruction.Input<Long> value) {
        return builder -> {
            value.write(builder);
            builder.write(LSTORE.var(index));
        };
    }

    public Instruction.Base floatValue(int index, Instruction.Input<Float> value) {
        return builder -> {
            value.write(builder);
            builder.write(FSTORE.var(index));
        };
    }

    public Instruction.Base doubleValue(int index, Instruction.Input<Double> value) {
        return builder -> {
            value.write(builder);
            builder.write(DSTORE.var(index));
        };
    }

    public Instruction.Base booleanValue(int index, Instruction.Input<Integer> value) {
        return this.intValue(index, value);
    }

}
