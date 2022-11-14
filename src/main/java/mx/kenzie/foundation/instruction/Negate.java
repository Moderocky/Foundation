package mx.kenzie.foundation.instruction;

import org.objectweb.asm.Opcodes;

public class Negate {
    Negate() {
    }
    
    public Instruction.Input invert(Instruction.Input value) {
        return visitor -> {
            value.write(visitor);
            visitor.visitInsn(Opcodes.ICONST_1);
            visitor.visitInsn(Opcodes.IXOR);
        };
    }
    
}
