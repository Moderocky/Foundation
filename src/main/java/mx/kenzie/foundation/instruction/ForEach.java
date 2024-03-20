package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import mx.kenzie.foundation.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ForEach {

    ForEach() {
    }

    public Block loop(Instruction.Base before, Instruction.Input<? extends Number> check, Instruction.Base after) {
        return new Block() {
            @Override
            public void write(MethodVisitor visitor) {
                before.write(visitor);
                visitor.visitLabel(start);
                check.write(visitor);
                visitor.visitJumpInsn(Opcodes.IFEQ, end);
                for (Instruction instruction : instructions) instruction.write(visitor);
                after.write(visitor);
                visitor.visitJumpInsn(Opcodes.GOTO, start);
                visitor.visitLabel(end);
            }
        };
    }

    public Block forEach(int var, Instruction.Input<Object> iterator, Type expected) {
        return new Block() {
            @Override
            public void write(MethodVisitor visitor) {
                visitor.visitInsn(Opcodes.ACONST_NULL);
                visitor.visitVarInsn(Opcodes.ASTORE, var);
                visitor.visitLabel(start);
                iterator.write(visitor);
                visitor.visitInsn(Opcodes.DUP);
                visitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
                visitor.visitJumpInsn(Opcodes.IFEQ, end);
                visitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;"
                    , true);
                visitor.visitTypeInsn(Opcodes.CHECKCAST, expected.internalName());
                visitor.visitVarInsn(Opcodes.ASTORE, var);
                for (Instruction instruction : instructions) instruction.write(visitor);
                visitor.visitJumpInsn(Opcodes.GOTO, start);
                visitor.visitLabel(end);
            }
        };
    }

}
