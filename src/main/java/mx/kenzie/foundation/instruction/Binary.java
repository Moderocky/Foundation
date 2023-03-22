package mx.kenzie.foundation.instruction;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;

import static mx.kenzie.foundation.instruction.Instruction.Operator.EQ;
import static org.objectweb.asm.Opcodes.*;

public class Binary {

    Binary() {
    }

    public Instruction.Input<Integer> objects(Instruction.Input<Object> a, Instruction.Operator operator, Instruction.Input<Object> b) {
        final int instruction = operator == EQ ? IF_ACMPNE : IF_ACMPEQ;
        return visitor -> {
            final Label fail = new Label(), end = new Label();
            a.write(visitor);
            b.write(visitor);
            visitor.visitJumpInsn(instruction, fail);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, end);
            visitor.visitLabel(fail);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(end);
        };
    }

    public Instruction.Input<Integer> ints(Instruction.Input<Integer> a, Instruction.Operator operator, Instruction.Input<Integer> b) {
        return switch (operator) {
            case OR -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(IOR);
            };
            case AND -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(IAND);
            };
            case XOR -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(IXOR);
            };
            case LESS -> equality(a, b, IF_ICMPGE);
            case GREATER -> equality(a, b, IF_ICMPLE);
            case LESS_EQ -> equality(a, b, IF_ICMPGT);
            case GREATER_EQ -> equality(a, b, IF_ICMPLT);
            case EQ -> equality(a, b, IF_ICMPNE);
            case NOT_EQ -> equality(a, b, IF_ICMPEQ);
        };
    }

    private Instruction.Input<Integer> equality(Instruction.Input<?> a, Instruction.Input<?> b, int instruction) {
        return visitor -> {
            final Label fail = new Label(), end = new Label();
            a.write(visitor);
            b.write(visitor);
            visitor.visitJumpInsn(instruction, fail);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, end);
            visitor.visitLabel(fail);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(end);
        };
    }

    public Instruction.Input<Integer> longs(Instruction.Input<Long> a, Instruction.Operator operator, Instruction.Input<Long> b) {
        return switch (operator) {
            case OR -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(LOR);
            };
            case AND -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(LAND);
            };
            case XOR -> visitor -> {
                a.write(visitor);
                b.write(visitor);
                visitor.visitInsn(LXOR);
            };
            case LESS -> equality(a, b, IFGE, LCMP);
            case GREATER -> equality(a, b, IFLE, LCMP);
            case LESS_EQ -> equality(a, b, IFGT, LCMP);
            case GREATER_EQ -> equality(a, b, IFLT, LCMP);
            case EQ -> equality(a, b, IFNE, LCMP);
            case NOT_EQ -> equality(a, b, IFEQ, LCMP);
        };
    }

    private Instruction.Input<Integer> equality(Instruction.Input<?> a, Instruction.Input<?> b, int instruction, int comparison) {
        return visitor -> {
            final Label fail = new Label(), end = new Label();
            a.write(visitor);
            b.write(visitor);
            visitor.visitInsn(comparison);
            visitor.visitJumpInsn(instruction, fail);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, end);
            visitor.visitLabel(fail);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(end);
        };
    }

    public Instruction.Input<Integer> floats(Instruction.Input<Number> a, Instruction.Operator operator, Instruction.Input<Number> b) {
        return this.compareFloating(a, operator, b, FCMPL);
    }

    @NotNull
    private Instruction.Input<Integer> compareFloating(Instruction.Input<Number> a, Instruction.Operator operator, Instruction.Input<Number> b, int comparator) {
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

    public Instruction.Input<Integer> doubles(Instruction.Input<Number> a, Instruction.Operator operator, Instruction.Input<Number> b) {
        return this.compareFloating(a, operator, b, DCMPL);
    }

}
