package mx.kenzie.foundation.instruction;

public class LoadConstant {
    
    LoadConstant() {
    }
    
    public Instruction.Input of(Object value) {
        return visitor -> visitor.visitLdcInsn(value);
    }
    
}
