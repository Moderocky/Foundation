package mx.kenzie.foundation.instruction;

import org.valross.foundation.assembler.code.Branch;
import org.valross.foundation.assembler.code.JumpCode;
import org.valross.foundation.assembler.code.UnboundedElement;
import org.jetbrains.annotations.NotNull;

import static org.valross.foundation.assembler.code.OpCode.*;
import static mx.kenzie.foundation.instruction.Instruction.Operator.EQ;

public class Binary {

    Binary() {
    }

    @NotNull
    static Instruction.Input<Integer> getIntegerInput(Instruction.Input<?> a, Instruction.Input<?> b,
                                                      JumpCode instruction) {
        return builder -> {
            final Branch fail = new Branch(), end = new Branch();
            a.write(builder);
            b.write(builder);
            builder.write(instruction.jump(fail), ICONST_1, GOTO.jump(end), fail, ICONST_0, end);
        };
    }

    @NotNull
    static Instruction.Input<Integer> getIntegerInput(Instruction.Input<?> a, Instruction.Input<?> b,
                                                      UnboundedElement comparison, JumpCode instruction) {
        return builder -> {
            final Branch fail = new Branch(), end = new Branch();
            a.write(builder);
            b.write(builder);
            builder.write(comparison, instruction.jump(fail));
            builder.write(ICONST_1, GOTO.jump(end), fail, ICONST_0, end);
        };
    }

    public Instruction.Input<Integer> objects(Instruction.Input<Object> a, Instruction.Operator operator,
                                              Instruction.Input<Object> b) {
        final JumpCode instruction = operator == EQ ? IF_ACMPNE : IF_ACMPEQ;
        return getIntegerInput(a, b, instruction);
    }

    public Instruction.Input<Integer> ints(Instruction.Input<Integer> a, Instruction.Operator operator,
                                           Instruction.Input<Integer> b) {
        return switch (operator) {
            case OR -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(IOR);
            };
            case AND -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(IAND);
            };
            case XOR -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(IXOR);
            };
            case LESS -> equality(a, b, IF_ICMPGE);
            case GREATER -> equality(a, b, IF_ICMPLE);
            case LESS_EQ -> equality(a, b, IF_ICMPGT);
            case GREATER_EQ -> equality(a, b, IF_ICMPLT);
            case EQ -> equality(a, b, IF_ICMPNE);
            case NOT_EQ -> equality(a, b, IF_ICMPEQ);
        };
    }

    private Instruction.Input<Integer> equality(Instruction.Input<?> a, Instruction.Input<?> b, JumpCode instruction) {
        return getIntegerInput(a, b, instruction);
    }

    public Instruction.Input<Integer> longs(Instruction.Input<Long> a, Instruction.Operator operator,
                                            Instruction.Input<Long> b) {
        return switch (operator) {
            case OR -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(LOR);
            };
            case AND -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(LAND);
            };
            case XOR -> builder -> {
                a.write(builder);
                b.write(builder);
                builder.write(LXOR);
            };
            case LESS -> equality(a, b, IFGE, LCMP);
            case GREATER -> equality(a, b, IFLE, LCMP);
            case LESS_EQ -> equality(a, b, IFGT, LCMP);
            case GREATER_EQ -> equality(a, b, IFLT, LCMP);
            case EQ -> equality(a, b, IFNE, LCMP);
            case NOT_EQ -> equality(a, b, IFEQ, LCMP);
        };
    }

    private Instruction.Input<Integer> equality(Instruction.Input<?> a, Instruction.Input<?> b, JumpCode instruction,
                                                UnboundedElement comparison) {
        return getIntegerInput(a, b, comparison, instruction);
    }

    public Instruction.Input<Integer> floats(Instruction.Input<? extends Number> a, Instruction.Operator operator,
                                             Instruction.Input<? extends Number> b) {
        return this.compareFloating(a, operator, b, FCMPL);
    }

    @NotNull
    private Instruction.Input<Integer> compareFloating(Instruction.Input<? extends Number> a, Instruction.Operator operator,
                                                       Instruction.Input<? extends Number> b, UnboundedElement comparator) {
        return switch (operator) {
            case LESS -> equality(a, b, IFGE, comparator);
            case GREATER -> equality(a, b, IFLE, comparator);
            case LESS_EQ -> equality(a, b, IFGT, comparator);
            case GREATER_EQ -> equality(a, b, IFLT, comparator);
            case EQ -> equality(a, b, IFNE, comparator);
            case NOT_EQ -> equality(a, b, IFEQ, comparator);
            default -> throw new RuntimeException("Unable to compare this number type in this way.");
        };
    }

    public Instruction.Input<Integer> doubles(Instruction.Input<? extends Number> a, Instruction.Operator operator,
                                              Instruction.Input<? extends Number> b) {
        return this.compareFloating(a, operator, b, DCMPL);
    }

}
