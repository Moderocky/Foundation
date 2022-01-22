package mx.kenzie.foundation;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class ConstructorBuilder extends MethodBuilder {
    
    public ConstructorBuilder(ClassBuilder builder) {
        super(builder, null);
    }
    
    @Override
    public MethodBuilder setReturnType(Class<?> type) {
        throw new IllegalStateException("Unable to alter constructor return type.");
    }
    
    @Override
    public MethodBuilder setReturnType(Type type) {
        throw new IllegalStateException("Unable to alter constructor return type.");
    }
    
    @Override
    void compile(ClassWriter writer) {
        final StringBuilder builder = new StringBuilder().append("(");
        for (Type parameter : parameters) {
            builder.append(parameter.descriptor());
        }
        builder.append(")V");
        final MethodVisitor methodVisitor;
        methodVisitor = writer.visitMethod(modifiers, "<init>", builder.toString(), null, null);
        methodVisitor.visitCode();
        if (this.writer.isEmpty()) {
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
        } else {
            this.writer.write(methodVisitor);
        }
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }
}
