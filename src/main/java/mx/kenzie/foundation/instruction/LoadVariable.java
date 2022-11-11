package mx.kenzie.foundation.instruction;

import static org.objectweb.asm.Opcodes.*;

public class LoadVariable {
    
    LoadVariable() {
    }
    
    public Instruction.Input object(int index) {
        return visitor -> visitor.visitVarInsn(ALOAD, index);
    }
    
    public Instruction.Input self() {
        return visitor -> visitor.visitVarInsn(ALOAD, 0);
    }
    
    public Instruction.Input byteValue(int index) {
        return this.intValue(index);
    }
    
    public Instruction.Input intValue(int index) {
        return visitor -> visitor.visitVarInsn(ILOAD, index);
    }
    
    public Instruction.Input shortValue(int index) {
        return this.intValue(index);
    }
    
    public Instruction.Input longValue(int index) {
        return visitor -> visitor.visitVarInsn(LLOAD, index);
    }
    
    public Instruction.Input floatValue(int index) {
        return visitor -> visitor.visitVarInsn(FLOAD, index);
    }
    
    public Instruction.Input doubleValue(int index) {
        return visitor -> visitor.visitVarInsn(DLOAD, index);
    }
    
    public Instruction.Input booleanValue(int index) {
        return this.intValue(index);
    }
    
}
