package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import org.objectweb.asm.Opcodes;

public class Conditional {
    
    Conditional() {
    }
    
    public Block check(Instruction.Input condition) {
        final Block block = new Block();
        block.line(((visitor, section) -> {
            condition.write(visitor);
            visitor.visitJumpInsn(Opcodes.IFEQ, section.end);
        }));
        return block;
    }
    
}
