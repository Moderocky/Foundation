package mx.kenzie.foundation.instruction;

import static org.valross.foundation.assembler.code.OpCode.*;
import static mx.kenzie.foundation.instruction.Binary.getIntegerInput;

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
        return getIntegerInput(a, b, LCMP, IFNE);
    }

    public Instruction.Input<Integer> floats(Instruction.Input<Float> a, Instruction.Input<Float> b) {
        return getIntegerInput(a, b, FCMPL, IFNE);
    }

    public Instruction.Input<Integer> doubles(Instruction.Input<Double> a, Instruction.Input<Double> b) {
        return getIntegerInput(a, b, DCMPL, IFNE);
    }

}
