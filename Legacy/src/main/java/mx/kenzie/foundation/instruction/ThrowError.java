package mx.kenzie.foundation.instruction;

import static org.valross.foundation.assembler.code.OpCode.ATHROW;

public class ThrowError {

    ThrowError() {
    }

    public Instruction.Base error(Instruction.Input<Object> error) {
        return builder -> {
            error.write(builder);
            builder.write(ATHROW);
        };
    }

}
