package mx.kenzie.foundation.instruction;

import org.objectweb.asm.Opcodes;

public class PushShort {
    PushShort() {
    }
    
    public Instruction.Input of(short s) {
        return visitor -> visitor.visitIntInsn(Opcodes.SIPUSH, s);
    }
}
