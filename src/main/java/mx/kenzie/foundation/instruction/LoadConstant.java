package mx.kenzie.foundation.instruction;

import org.objectweb.asm.Opcodes;

public class LoadConstant {

    LoadConstant() {
    }

    public <Result extends Number> Instruction.Input<Result> of(Result value) {
        return visitor -> {
            if (value instanceof Byte b) visitor.visitIntInsn(Opcodes.BIPUSH, b);
            if (value instanceof Short s) visitor.visitIntInsn(Opcodes.SIPUSH, s);
            if (value instanceof Integer i && i < Short.MAX_VALUE && i > Short.MIN_VALUE)
                visitor.visitIntInsn(Opcodes.SIPUSH, i);
            visitor.visitLdcInsn(value);
        };
    }

    public Instruction.Input<Object> of(Object value) {
        return visitor -> visitor.visitLdcInsn(value);
    }

}
