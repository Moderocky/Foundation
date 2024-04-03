package mx.kenzie.foundation.instruction;

import org.valross.foundation.assembler.code.OpCode;

public class Increment {

    Increment() {
    }

    public Instruction.Base var(int var, int amount) {
        return builder -> builder.write(OpCode.IINC.var(var, amount));
    }

}
