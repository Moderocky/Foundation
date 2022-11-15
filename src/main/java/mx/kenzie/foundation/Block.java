package mx.kenzie.foundation;

import mx.kenzie.foundation.instruction.Instruction;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.LinkedList;
import java.util.List;

public class Block implements CodeBody, Instruction.Base {
    
    public final Label start = new Label(), end = new Label();
    protected List<Instruction> instructions = new LinkedList<>();
    
    public void line(Instruction.Block instruction) {
        this.line(visitor -> instruction.write(visitor, this));
    }
    
    @Override
    public void line(Instruction.Base instruction) {
        this.instructions.add(instruction);
    }
    
    @Override
    public Instruction[] lines() {
        return instructions.toArray(new Instruction[0]);
    }
    
    @Override
    public void write(MethodVisitor visitor) {
        visitor.visitLabel(start);
        for (Instruction instruction : instructions) instruction.write(visitor);
        visitor.visitLabel(end);
    }
    
    public static class If extends mx.kenzie.foundation.Block {
        protected Instruction.Input condition;
        protected mx.kenzie.foundation.Block elseBlock;
        
        public If(Instruction.Input condition) {
            this.condition = condition;
        }
        
        public mx.kenzie.foundation.Block elseBlock() {
            return elseBlock != null ? elseBlock : (elseBlock = new mx.kenzie.foundation.Block());
        }
        
        @Override
        public void write(MethodVisitor visitor) {
            if (elseBlock == null) {
                this.condition.write(visitor);
                visitor.visitJumpInsn(Opcodes.IFEQ, end);
                for (Instruction instruction : instructions) instruction.write(visitor);
                visitor.visitLabel(end);
            } else {
                this.condition.write(visitor);
                visitor.visitJumpInsn(Opcodes.IFEQ, elseBlock.start);
                for (Instruction instruction : instructions) instruction.write(visitor);
                visitor.visitJumpInsn(Opcodes.GOTO, elseBlock.end);
                this.elseBlock.write(visitor);
            }
        }
    }
}
