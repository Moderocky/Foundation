package mx.kenzie.foundation.instruction;

import org.objectweb.asm.Opcodes;

public class Push {

    Push() {
    }

    public Instruction.Input<Integer> byteValue(int b) {
        return visitor -> visitor.visitIntInsn(Opcodes.BIPUSH, b);
    }

    public Instruction.Input<Integer> shortValue(int s) {
        return visitor -> visitor.visitIntInsn(Opcodes.SIPUSH, s);
    }

}
