package mx.kenzie.foundation.instruction;

import static org.valross.foundation.assembler.code.OpCode.ICONST_1;
import static org.valross.foundation.assembler.code.OpCode.IXOR;

public class Negate {

    Negate() {
    }

    public Instruction.Input<Integer> invert(Instruction.Input<Integer> value) {
        return builder -> {
            value.write(builder);
            builder.write(ICONST_1, IXOR);
        };
    }

}
