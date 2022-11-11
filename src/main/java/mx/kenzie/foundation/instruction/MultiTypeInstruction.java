package mx.kenzie.foundation.instruction;

public abstract class MultiTypeInstruction {
    public abstract Instruction object(Instruction.Input instruction);
    
    public Instruction byteValue(Instruction.Input instruction) {
        return this.intValue(instruction);
    }
    
    public abstract Instruction intValue(Instruction.Input instruction);
    
    public Instruction shortValue(Instruction.Input instruction) {
        return this.intValue(instruction);
    }
    
    public abstract Instruction longValue(Instruction.Input instruction);
    
    public abstract Instruction floatValue(Instruction.Input instruction);
    
    public abstract Instruction doubleValue(Instruction.Input instruction);
    
    public Instruction booleanValue(Instruction.Input instruction) {
        return this.intValue(instruction);
    }
    
    public abstract Instruction none();
}
