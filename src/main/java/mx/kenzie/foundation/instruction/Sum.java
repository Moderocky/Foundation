package mx.kenzie.foundation.instruction;

import static org.objectweb.asm.Opcodes.*;

public class Sum {
    
    Sum() {
    }
    
    public Instruction.Input ints(Instruction.Input a, Instruction.Math operator, Instruction.Input b) {
        return switch (operator) {
            case PLUS -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(IADD);
            };
            case MINUS -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(ISUB);
            };
            case TIMES -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(IMUL);
            };
            case DIVIDED -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(IDIV);
            };
        };
    }
    
    public Instruction.Input longs(Instruction.Input a, Instruction.Math operator, Instruction.Input b) {
        return switch (operator) {
            case PLUS -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(LADD);
            };
            case MINUS -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(LSUB);
            };
            case TIMES -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(LMUL);
            };
            case DIVIDED -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(LDIV);
            };
        };
    }
    
    public Instruction.Input floats(Instruction.Input a, Instruction.Math operator, Instruction.Input b) {
        return switch (operator) {
            case PLUS -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(FADD);
            };
            case MINUS -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(FSUB);
            };
            case TIMES -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(FMUL);
            };
            case DIVIDED -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(FDIV);
            };
        };
    }
    
    public Instruction.Input doubles(Instruction.Input a, Instruction.Math operator, Instruction.Input b) {
        return switch (operator) {
            case PLUS -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(DADD);
            };
            case MINUS -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(DSUB);
            };
            case TIMES -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(DMUL);
            };
            case DIVIDED -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(DDIV);
            };
        };
    }
    
}
