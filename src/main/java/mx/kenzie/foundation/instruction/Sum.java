package mx.kenzie.foundation.instruction;

import static org.objectweb.asm.Opcodes.*;

public class Sum {

    Sum() {
    }

    public Instruction.Input<Integer> ints(Instruction.Input<Integer> a, Instruction.Math operator, Instruction.Input<Integer> b) {
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

    public Instruction.Input<Long> longs(Instruction.Input<Long> a, Instruction.Math operator, Instruction.Input<Long> b) {
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

    public Instruction.Input<Float> floats(Instruction.Input<Float> a, Instruction.Math operator, Instruction.Input<Float> b) {
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

    public Instruction.Input<Double> doubles(Instruction.Input<Double> a, Instruction.Math operator, Instruction.Input<Double> b) {
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
