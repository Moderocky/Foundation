package mx.kenzie.foundation;

import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

interface SimpleWriteInstruction extends Consumer<MethodVisitor>, WriteInstruction {
    
    @Override
    default void accept(CodeWriter codeWriter, MethodVisitor methodVisitor) {
        accept(methodVisitor);
    }
    
}
