package mx.kenzie.foundation;

import mx.kenzie.foundation.instruction.Instruction;

public interface CodeBody {
    
    void line(Instruction.Base instruction);
    
    Instruction[] lines();
    
}
