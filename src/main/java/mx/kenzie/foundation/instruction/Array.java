package mx.kenzie.foundation.instruction;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Type;

import static org.objectweb.asm.Opcodes.*;

public class Array {

    Array() {
    }

    @SafeVarargs
    public final <Klass extends Type & TypeDescriptor> Instruction.Input<Object> of(Klass type,
                                                                                    Instruction.Input<Object>... values) {
        final mx.kenzie.foundation.Type found = mx.kenzie.foundation.Type.of(type);
        final int length = values.length;
        return visitor -> {
            visitor.visitIntInsn(SIPUSH, length);
            visitor.visitTypeInsn(ANEWARRAY, found.internalName());
            for (int i = 0; i < length; i++) {
                visitor.visitInsn(DUP);
                visitor.visitIntInsn(SIPUSH, i);
                values[i].write(visitor);
                visitor.visitInsn(AASTORE);
            }
        };
    }

}
