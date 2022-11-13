package mx.kenzie.foundation.instruction;

import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;

public class Equals {
    
    Equals() {
    }
    
    public Instruction.Input objects(Instruction.Input a, Instruction.Input b) {
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
    
    public Instruction.Input ints(Instruction.Input a, Instruction.Input b) {
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
    
    public Instruction.Input longs(Instruction.Input a, Instruction.Input b) {
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
    
    public Instruction.Input floats(Instruction.Input a, Instruction.Input b) {
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
    
    public Instruction.Input doubles(Instruction.Input a, Instruction.Input b) {
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
