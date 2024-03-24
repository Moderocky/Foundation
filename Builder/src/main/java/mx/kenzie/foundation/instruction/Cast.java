package mx.kenzie.foundation.instruction;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Type;

import static org.objectweb.asm.Opcodes.CHECKCAST;

public class Cast {

    Cast() {
    }

    public <Result extends Number, Input extends Number> Instruction.Input<Result> number(Instruction.Input<Input> value, Instruction.Convert convert) {
        return visitor -> {
            value.write(visitor);
            visitor.visitInsn(convert.opcode);
        };
    }

    public <Klass extends Type & TypeDescriptor> Instruction.Input<Object> object(Instruction.Input<Object> value,
                                                                                  Klass type) {
        final mx.kenzie.foundation.detail.Type found = mx.kenzie.foundation.detail.Type.of(type);
        return visitor -> {
            value.write(visitor);
            visitor.visitTypeInsn(CHECKCAST, found.internalName());
        };
    }

}
