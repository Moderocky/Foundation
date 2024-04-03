package mx.kenzie.foundation.instruction;

public abstract class MultiTypeInstruction {

    public abstract Instruction object(Instruction.Input<Object> instruction);

    public Instruction byteValue(Instruction.Input<Integer> instruction) {
        return this.intValue(instruction);
    }

    public abstract Instruction intValue(Instruction.Input<Integer> instruction);

    public Instruction shortValue(Instruction.Input<Integer> instruction) {
        return this.intValue(instruction);
    }

    public abstract Instruction longValue(Instruction.Input<Long> instruction);

    public abstract Instruction floatValue(Instruction.Input<Float> instruction);

    public abstract Instruction doubleValue(Instruction.Input<Double> instruction);

    public Instruction booleanValue(Instruction.Input<Integer> instruction) {
        return this.intValue(instruction);
    }

    public abstract Instruction none();

}
