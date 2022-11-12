package mx.kenzie.foundation.instruction;

import org.objectweb.asm.Opcodes;

public class ThrowError {
    ThrowError() {
    }
    
    public Instruction.Base error(Instruction.Input error) {
        return visitor -> {
            error.write(visitor);
            visitor.visitInsn(Opcodes.ATHROW);
        };
    }
}
