package mx.kenzie.foundation.instruction;

import static org.objectweb.asm.Opcodes.*;

public class Return extends MultiTypeInstruction {
    
    public Return() {
    }
    
    @Override
    public Instruction.Base object(Instruction.Input instruction) {
        return visitor -> {
            instruction.write(visitor);
            visitor.visitInsn(ARETURN);
        };
    }
    
    @Override
    public Instruction.Base intValue(Instruction.Input instruction) {
        return visitor -> {
            instruction.write(visitor);
            visitor.visitInsn(IRETURN);
        };
    }
    
    @Override
    public Instruction.Base longValue(Instruction.Input instruction) {
        return visitor -> {
            instruction.write(visitor);
            visitor.visitInsn(LRETURN);
        };
    }
    
    @Override
    public Instruction.Base floatValue(Instruction.Input instruction) {
        return visitor -> {
            instruction.write(visitor);
            visitor.visitInsn(FRETURN);
        };
    }
    
    @Override
    public Instruction.Base doubleValue(Instruction.Input instruction) {
        return visitor -> {
            instruction.write(visitor);
            visitor.visitInsn(DRETURN);
        };
    }
    
    @Override
    public Instruction.Base none() {
        return visitor -> visitor.visitInsn(RETURN);
    }
}
