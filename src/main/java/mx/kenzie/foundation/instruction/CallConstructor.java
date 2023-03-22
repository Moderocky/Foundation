package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Type;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.TypeDescriptor;

public class CallConstructor {

    CallConstructor() {
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass type, Klass... parameters) {
        return new Stub(Type.of(type), Type.array(parameters));
    }

    public record Stub(Type owner, Type... parameters) {

        public Instruction.Input<Object> make(Instruction.Input<?>... arguments) {
            return visitor -> {
                visitor.visitTypeInsn(Opcodes.NEW, owner.internalName());
                visitor.visitInsn(Opcodes.DUP);
                for (Instruction.Input<?> argument : arguments) argument.write(visitor);
                visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, owner.internalName(), "<init>", Type.methodDescriptor(Type.VOID, parameters), false);
            };

        }

    }

}
