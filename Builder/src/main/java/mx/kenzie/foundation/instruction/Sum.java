package mx.kenzie.foundation.instruction;

import static mx.kenzie.foundation.assembler.code.OpCode.*;

public class Sum {

    Sum() {
    }

    public Instruction.Input<Integer> ints(Instruction.Input<Integer> a, Instruction.Math operator,
                                           Instruction.Input<Integer> b) {
        return switch (operator) {
            case PLUS -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(IADD);
            };
            case MINUS -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(ISUB);
            };
            case TIMES -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(IMUL);
            };
            case DIVIDED -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(IDIV);
            };
        };
    }

    public Instruction.Input<Long> longs(Instruction.Input<Long> a, Instruction.Math operator,
                                         Instruction.Input<Long> b) {
        return switch (operator) {
            case PLUS -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(LADD);
            };
            case MINUS -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(LSUB);
            };
            case TIMES -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(LMUL);
            };
            case DIVIDED -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(LDIV);
            };
        };
    }

    public Instruction.Input<Float> floats(Instruction.Input<Float> a, Instruction.Math operator,
                                           Instruction.Input<Float> b) {
        return switch (operator) {
            case PLUS -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(FADD);
            };
            case MINUS -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(FSUB);
            };
            case TIMES -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(FMUL);
            };
            case DIVIDED -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(FDIV);
            };
        };
    }

    public Instruction.Input<Double> doubles(Instruction.Input<Double> a, Instruction.Math operator,
                                             Instruction.Input<Double> b) {
        return switch (operator) {
            case PLUS -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(DADD);
            };
            case MINUS -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(DSUB);
            };
            case TIMES -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(DMUL);
            };
            case DIVIDED -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(DDIV);
            };
        };
    }

}
