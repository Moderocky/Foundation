package mx.kenzie.foundation.instruction;

import static mx.kenzie.foundation.instruction.Binary.getIntegerInput;
import static org.objectweb.asm.Opcodes.*;

public class Equals {

    Equals() {
    }

    public Instruction.Input<Integer> objects(Instruction.Input<Object> a, Instruction.Input<Object> b) {
        return getIntegerInput(a, b, IF_ACMPNE);
    }

    public Instruction.Input<Integer> ints(Instruction.Input<Integer> a, Instruction.Input<Integer> b) {
        return getIntegerInput(a, b, IF_ICMPNE);
    }

    public Instruction.Input<Integer> longs(Instruction.Input<Long> a, Instruction.Input<Long> b) {
        return getIntegerInput(a, b, IFNE, LCMP);
    }

    public Instruction.Input<Integer> floats(Instruction.Input<Float> a, Instruction.Input<Float> b) {
        return getIntegerInput(a, b, IFNE, FCMPL);
    }

    public Instruction.Input<Integer> doubles(Instruction.Input<Double> a, Instruction.Input<Double> b) {
        return getIntegerInput(a, b, IFNE, DCMPL);
    }

}
