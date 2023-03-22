package mx.kenzie.foundation.instruction;

import static org.objectweb.asm.Opcodes.*;

public class LoadVariable {

    LoadVariable() {
    }

    public Instruction.Input<Object> object(int index) {
        return visitor -> visitor.visitVarInsn(ALOAD, index);
    }

    public Instruction.Input<Object> self() {
        return visitor -> visitor.visitVarInsn(ALOAD, 0);
    }

    public Instruction.Input<Integer> byteValue(int index) {
        return this.intValue(index);
    }

    public Instruction.Input<Integer> intValue(int index) {
        return visitor -> visitor.visitVarInsn(ILOAD, index);
    }

    public Instruction.Input<Integer> shortValue(int index) {
        return this.intValue(index);
    }

    public Instruction.Input<Long> longValue(int index) {
        return visitor -> visitor.visitVarInsn(LLOAD, index);
    }

    public Instruction.Input<Float> floatValue(int index) {
        return visitor -> visitor.visitVarInsn(FLOAD, index);
    }

    public Instruction.Input<Double> doubleValue(int index) {
        return visitor -> visitor.visitVarInsn(DLOAD, index);
    }

    public Instruction.Input<Integer> booleanValue(int index) {
        return this.intValue(index);
    }

}
