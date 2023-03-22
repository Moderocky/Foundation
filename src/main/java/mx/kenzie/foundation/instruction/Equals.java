package mx.kenzie.foundation.instruction;

import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;

public class Equals {

    Equals() {
    }

    public Instruction.Input<Integer> objects(Instruction.Input<Object> a, Instruction.Input<Object> b) {
        return visitor -> {
            final Label fail = new Label(), end = new Label();
            a.write(visitor);
            b.write(visitor);
            visitor.visitJumpInsn(IF_ACMPNE, fail);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, end);
            visitor.visitLabel(fail);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(end);
        };
    }

    public Instruction.Input<Integer> ints(Instruction.Input<Integer> a, Instruction.Input<Integer> b) {
        return visitor -> {
            final Label fail = new Label(), end = new Label();
            a.write(visitor);
            b.write(visitor);
            visitor.visitJumpInsn(IF_ICMPNE, fail);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, end);
            visitor.visitLabel(fail);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(end);
        };
    }

    public Instruction.Input<Integer> longs(Instruction.Input<Long> a, Instruction.Input<Long> b) {
        return visitor -> {
            final Label fail = new Label(), end = new Label();
            a.write(visitor);
            b.write(visitor);
            visitor.visitInsn(LCMP);
            visitor.visitJumpInsn(IFNE, fail);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, end);
            visitor.visitLabel(fail);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(end);
        };
    }

    public Instruction.Input<Integer> floats(Instruction.Input<Float> a, Instruction.Input<Float> b) {
        return visitor -> {
            final Label fail = new Label(), end = new Label();
            a.write(visitor);
            b.write(visitor);
            visitor.visitInsn(FCMPL);
            visitor.visitJumpInsn(IFNE, fail);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, end);
            visitor.visitLabel(fail);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(end);
        };
    }

    public Instruction.Input<Integer> doubles(Instruction.Input<Double> a, Instruction.Input<Double> b) {
        return visitor -> {
            final Label fail = new Label(), end = new Label();
            a.write(visitor);
            b.write(visitor);
            visitor.visitInsn(DCMPL);
            visitor.visitJumpInsn(IFNE, fail);
            visitor.visitInsn(ICONST_1);
            visitor.visitJumpInsn(GOTO, end);
            visitor.visitLabel(fail);
            visitor.visitInsn(ICONST_0);
            visitor.visitLabel(end);
        };
    }

}
