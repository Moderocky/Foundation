package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class While extends Conditional {
    
    While() {
    }
    
    @Override
    public Block check(Instruction.Input condition) {
        return new Block() {
            @Override
            public void write(MethodVisitor visitor) {
                visitor.visitLabel(start);
                condition.write(visitor);
                visitor.visitJumpInsn(Opcodes.IFEQ, end);
                for (Instruction instruction : instructions) instruction.write(visitor);
                visitor.visitJumpInsn(Opcodes.GOTO, start);
                visitor.visitLabel(end);
            }
        };
    }
    
    public Block doWhile(Instruction.Input condition) {
        return new Block() {
            @Override
            public void write(MethodVisitor visitor) {
                visitor.visitLabel(start);
                for (Instruction instruction : instructions) instruction.write(visitor);
                condition.write(visitor);
                visitor.visitJumpInsn(Opcodes.IFNE, start);
                visitor.visitLabel(end);
            }
        };
    }
    
}
