package mx.kenzie.foundation;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

/**
 * Used for controlled cloning of an existing method from available source.
 */
class CloneMethodVisitor extends MethodVisitor {
    
    final List<WriteInstruction> instructions;
    final RewriteController controller;
    boolean skipReturn;
    boolean hasController;
    
    CloneMethodVisitor(List<WriteInstruction> instructions, int api, MethodVisitor methodVisitor, RewriteController controller) {
        super(api, methodVisitor);
        this.controller = controller;
        this.instructions = instructions;
        this.skipReturn = controller != null && controller.isInline();
        this.hasController = controller != null;
    }
    
    @Override
    public void visitAttribute(Attribute attribute) {
        instructions.add((writer, visitor) -> visitor.visitAttribute(attribute));
    }
    
    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        instructions.add((writer, visitor) -> visitor.visitFrame(type, numLocal, local, numStack, stack));
    }
    
    @Override
    public void visitInsn(int opcode) {
        if (controller != null && opcode >= 172 && opcode <= 177) {
            controller.markReturn();
            if (skipReturn) {
                instructions.add(controller.return0(opcode));
            }
        } else
            instructions.add((writer, visitor) -> visitor.visitInsn(opcode));
        write("visitInsn", opcode, null);
    }
    
    private void write(String mode, int opcode, Object data) {
        if (hasController) controller.write(mode, opcode, data);
    }
    
    @Override
    public void visitIntInsn(int opcode, int operand) {
        instructions.add((writer, visitor) -> visitor.visitIntInsn(opcode, operand));
        write("visitIntInsn", opcode, operand);
    }
    
    @Override
    public void visitVarInsn(int opcode, int var) {
        if (controller != null && opcode >= 21 && opcode <= 25) {
            if (skipReturn) {
                instructions.add(controller.load(opcode, var));
            }
        } else if (controller != null && opcode >= 54 && opcode <= 58) {
            if (skipReturn) {
                instructions.add(controller.store(opcode, var));
            }
        } else
            instructions.add((writer, visitor) -> visitor.visitVarInsn(opcode, var));
        write("visitVarInsn", opcode, var);
    }
    
    @Override
    public void visitTypeInsn(int opcode, String type) {
        instructions.add((writer, visitor) -> visitor.visitTypeInsn(opcode, type));
        write("visitTypeInsn", opcode, type);
    }
    
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (controller != null) controller.useField(Type.of(owner), name);
        instructions.add((writer, visitor) -> visitor.visitFieldInsn(opcode, owner, name, descriptor));
        write("visitFieldInsn", opcode, name);
    }
    
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
        if (controller != null) controller.useMethod(Type.of(owner), name);
        instructions.add((writer, visitor) -> visitor.visitMethodInsn(opcode, owner, name, descriptor));
        write("visitMethodInsn", opcode, name);
    }
    
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (controller != null) controller.useMethod(Type.of(owner), name);
        instructions.add((writer, visitor) -> visitor.visitMethodInsn(opcode, owner, name, descriptor, isInterface));
        write("visitMethodInsn", opcode, name);
    }
    
    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        instructions.add((writer, visitor) -> visitor.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments));
    }
    
    @Override
    public void visitJumpInsn(int opcode, Label label) {
        instructions.add((writer, visitor) -> visitor.visitJumpInsn(opcode, label));
        write("visitJumpInsn", opcode, label);
    }
    
    @Override
    public void visitLabel(Label label) {
        instructions.add((writer, visitor) -> visitor.visitLabel(label));
        write("visitLabel", -2, label);
    }
    
    @Override
    public void visitLdcInsn(Object value) {
        instructions.add((writer, visitor) -> visitor.visitLdcInsn(value));
        write("visitLdcInsn", -3, value);
    }
    
    @Override
    public void visitIincInsn(int var, int increment) {
        instructions.add((writer, visitor) -> visitor.visitIincInsn(var, increment));
        write("visitIincInsn", var, increment);
    }
    
    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        instructions.add((writer, visitor) -> visitor.visitTableSwitchInsn(min, max, dflt, labels));
    }
    
    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        instructions.add((writer, visitor) -> visitor.visitLookupSwitchInsn(dflt, keys, labels));
    }
    
    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        instructions.add((writer, visitor) -> visitor.visitMultiANewArrayInsn(descriptor, numDimensions));
    }
    
    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        instructions.add((writer, visitor) -> visitor.visitTryCatchBlock(start, end, handler, type));
    }
    
}
