package mx.kenzie.foundation.instruction;

public class Increment {

    Increment() {
    }

    public Instruction.Base var(int var, int amount) {
        return visitor -> visitor.visitIincInsn(var, amount);
    }

}
