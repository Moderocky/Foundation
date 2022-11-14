package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Type;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Method;

public class CallMethod {
    
    CallMethod() {
    }
    
    public Stub of(Class<?> owner, String name, Class<?>... parameters) {
        try {
            final Method method = owner.getMethod(name, parameters);
            return of(owner, method.getReturnType(), name, parameters);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    
    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass owner, Klass returnType, String name, Klass... parameters) {
        return new Stub(Type.of(owner), Type.of(returnType), name, Type.array(parameters));
    }
    
    public record Stub(Type owner, Type returnType, String name, Type... parameters) {
        public Instruction.Base callStatic(Instruction.Input... arguments) {
            return visitor -> {
                for (Instruction.Input argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), false);
                if (returnType != Type.VOID) visitor.visitInsn(Opcodes.POP);
            };
        }
        
        public Instruction.Input getStatic(Instruction.Input... arguments) {
            return visitor -> {
                for (Instruction.Input argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), false);
                if (returnType == Type.VOID) visitor.visitInsn(Opcodes.ACONST_NULL);
            };
        }
        
        public Instruction.Base call(Instruction.Input object, Instruction.Input... arguments) {
            return visitor -> {
                object.write(visitor);
                for (Instruction.Input argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), false);
                if (returnType != Type.VOID) visitor.visitInsn(Opcodes.POP);
            };
        }
        
        public Instruction.Input get(Instruction.Input object, Instruction.Input... arguments) {
            return visitor -> {
                object.write(visitor);
                for (Instruction.Input argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner.internalName(), name, Type.methodDescriptor(returnType, parameters), false);
                if (returnType == Type.VOID) visitor.visitInsn(Opcodes.ACONST_NULL);
            };
        }
    }
    
}
