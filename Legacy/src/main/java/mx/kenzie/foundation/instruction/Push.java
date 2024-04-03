package mx.kenzie.foundation.instruction;

import static org.valross.foundation.assembler.code.OpCode.*;

public class Push {

    Push() {
    }

    public Instruction.Input<Integer> byteValue(int b) {
        return builder -> builder.write(BIPUSH.value(b));
    }

    public Instruction.Input<Integer> shortValue(int s) {
        return builder -> builder.write(SIPUSH.value(s));
    }

    public Instruction.Input<Integer> value(int i) {
        if (i < Short.MIN_VALUE || i > Short.MAX_VALUE) return builder -> builder.write(LDC.value(i));
        return builder -> builder.write(SIPUSH.value(i));
    }

}
