package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.assembler.code.OpCode;

public class Increment {

    Increment() {
    }

    public Instruction.Base var(int var, int amount) {
        return builder -> builder.write(OpCode.IINC.var(var, amount));
    }

}
