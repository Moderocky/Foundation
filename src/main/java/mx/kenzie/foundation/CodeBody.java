package mx.kenzie.foundation;

import mx.kenzie.foundation.instruction.Instruction;

public interface CodeBody {

    void line(Instruction.Base instruction);

    default Instruction getLine(int line) {
        return this.lines()[line];
    }

    Instruction[] lines();

}
