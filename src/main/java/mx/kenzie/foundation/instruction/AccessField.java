package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Type;

import java.lang.invoke.TypeDescriptor;

import static org.objectweb.asm.Opcodes.*;

public class AccessField {
    
    AccessField() {
    }
    
    public <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass owner, String name, Klass type) {
        return new Stub(Type.of(owner), Type.of(type), name);
    }
    
    public record Stub(Type owner, Type type, String name) {
        
        public Instruction.Input get() {
            return visitor -> visitor.visitFieldInsn(GETSTATIC, owner.internalName(), name, type.descriptorString());
        }
        
        public Instruction.Input get(Instruction.Input object) {
            return visitor -> {
                object.write(visitor);
                visitor.visitFieldInsn(GETFIELD, owner.internalName(), name, type.descriptorString());
            };
        }
        
        public Instruction.Base set(Instruction.Input value) {
            return visitor -> {
                value.write(visitor);
                visitor.visitFieldInsn(PUTSTATIC, owner.internalName(), name, type.descriptorString());
            };
        }
        
        public Instruction.Base set(Instruction.Input object, Instruction.Input value) {
            return visitor -> {
                object.write(visitor);
                value.write(visitor);
                visitor.visitFieldInsn(PUTFIELD, owner.internalName(), name, type.descriptorString());
            };
        }
        
    }
    
}
