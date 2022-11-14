package mx.kenzie.foundation.instruction;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Type;

import static org.objectweb.asm.Opcodes.CHECKCAST;

public class Cast {
    
    Cast() {
    }
    
    public Instruction.Input number(Instruction.Input value, Instruction.Convert convert) {
        return visitor -> {
            value.write(visitor);
            visitor.visitInsn(convert.opcode);
        };
    }
    
    public <Klass extends Type & TypeDescriptor> Instruction.Input object(Instruction.Input value, Klass type) {
        final mx.kenzie.foundation.Type found = mx.kenzie.foundation.Type.of(type);
        return visitor -> {
            value.write(visitor);
            visitor.visitTypeInsn(CHECKCAST, found.internalName());
        };
    }
    
}
