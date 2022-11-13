package mx.kenzie.foundation.instruction;

import org.objectweb.asm.Opcodes;

public class PushByte {
    PushByte() {
    }
    
    public Instruction.Input of(int b) {
        return visitor -> visitor.visitIntInsn(Opcodes.BIPUSH, b);
    }
}
