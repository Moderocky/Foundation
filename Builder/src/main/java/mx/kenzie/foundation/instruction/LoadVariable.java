package mx.kenzie.foundation.instruction;

import static mx.kenzie.foundation.assembler.code.OpCode.*;

public class LoadVariable {

    LoadVariable() {
    }

    public Instruction.Input<Object> object(int index) {
        return builder -> builder.write(ALOAD.var(index));
    }

    public Instruction.Input<Object> self() {
        return builder -> builder.write(ALOAD_0);
    }

    public Instruction.Input<Integer> byteValue(int index) {
        return this.intValue(index);
    }

    public Instruction.Input<Integer> intValue(int index) {
        return builder -> builder.write(ILOAD.var(index));
    }

    public Instruction.Input<Integer> shortValue(int index) {
        return this.intValue(index);
    }

    public Instruction.Input<Long> longValue(int index) {
        return builder -> builder.write(LLOAD.var(index));
    }

    public Instruction.Input<Float> floatValue(int index) {
        return builder -> builder.write(FLOAD.var(index));
    }

    public Instruction.Input<Double> doubleValue(int index) {
        return builder -> builder.write(DLOAD.var(index));
    }

    public Instruction.Input<Integer> booleanValue(int index) {
        return this.intValue(index);
    }

}
