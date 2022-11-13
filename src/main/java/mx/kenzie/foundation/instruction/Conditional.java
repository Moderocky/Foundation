package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

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
    
    public Block compareInts(Instruction.Input a, Instruction.Operator operator, Instruction.Input b) {
        return new Block() {
            @Override
            public void write(MethodVisitor visitor) {
                visitor.visitLabel(start);
                a.write(visitor);
                b.write(visitor);
                switch (operator) {
                    case OR -> visitor.visitInsn(IOR);
                    case AND -> visitor.visitInsn(IAND);
                    case XOR -> visitor.visitInsn(IXOR);
                }
                switch (operator) {
                    case LESS -> visitor.visitJumpInsn(IF_ICMPGE, end);
                    case GREATER -> visitor.visitJumpInsn(IF_ICMPLE, end);
                    case LESS_EQ -> visitor.visitJumpInsn(IF_ICMPGT, end);
                    case GREATER_EQ -> visitor.visitJumpInsn(IF_ICMPLT, end);
                    case EQ -> visitor.visitJumpInsn(IF_ICMPNE, end);
                    case NOT_EQ -> visitor.visitJumpInsn(IF_ICMPEQ, end);
                    case OR, AND, XOR -> visitor.visitJumpInsn(IFEQ, end);
                }
                for (Instruction instruction : instructions) instruction.write(visitor);
                visitor.visitLabel(end);
            }
        };
    }
    
}
