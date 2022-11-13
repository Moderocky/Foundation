package mx.kenzie.foundation.instruction;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;

import static mx.kenzie.foundation.instruction.Instruction.Operator.EQ;
import static org.objectweb.asm.Opcodes.*;

public class Binary {
    
    Binary() {
    }
    
    public Instruction.Input objects(Instruction.Input a, Instruction.Operator operator, Instruction.Input b) {
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
    
    public Instruction.Input ints(Instruction.Input a, Instruction.Operator operator, Instruction.Input b) {
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
    
    private Instruction.Input equality(Instruction.Input a, Instruction.Input b, int instruction) {
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
    
    public Instruction.Input longs(Instruction.Input a, Instruction.Operator operator, Instruction.Input b) {
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
    
    private Instruction.Input equality(Instruction.Input a, Instruction.Input b, int instruction, int comparison) {
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
    
    public Instruction.Input floats(Instruction.Input a, Instruction.Operator operator, Instruction.Input b) {
        return this.compareFloating(a, operator, b, FCMPL);
    }
    
    @NotNull
    private Instruction.Input compareFloating(Instruction.Input a, Instruction.Operator operator, Instruction.Input b, int comparator) {
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
    
    public Instruction.Input doubles(Instruction.Input a, Instruction.Operator operator, Instruction.Input b) {
        return this.compareFloating(a, operator, b, DCMPL);
    }
    
}
