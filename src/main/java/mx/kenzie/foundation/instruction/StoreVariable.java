package mx.kenzie.foundation.instruction;

import static org.objectweb.asm.Opcodes.*;

public class StoreVariable {
    
    StoreVariable() {
    }
    
    public Instruction.Base object(int index, Instruction.Input value) {
        return visitor -> {
            value.write(visitor);
            visitor.visitVarInsn(ASTORE, index);
        };
    }
    
    public Instruction.Base byteValue(int index, Instruction.Input value) {
        return this.intValue(index, value);
    }
    
    public Instruction.Base intValue(int index, Instruction.Input value) {
        return visitor -> {
            value.write(visitor);
            visitor.visitVarInsn(ISTORE, index);
        };
    }
    
    public Instruction.Base shortValue(int index, Instruction.Input value) {
        return this.intValue(index, value);
    }
    
    public Instruction.Base longValue(int index, Instruction.Input value) {
        return visitor -> {
            value.write(visitor);
            visitor.visitVarInsn(LSTORE, index);
        };
    }
    
    public Instruction.Base floatValue(int index, Instruction.Input value) {
        return visitor -> {
            value.write(visitor);
            visitor.visitVarInsn(FSTORE, index);
        };
    }
    
    public Instruction.Base doubleValue(int index, Instruction.Input value) {
        return visitor -> {
            value.write(visitor);
            visitor.visitVarInsn(DSTORE, index);
        };
    }
    
    public Instruction.Base booleanValue(int index, Instruction.Input value) {
        return this.intValue(index, value);
    }
}
