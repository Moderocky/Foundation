package mx.kenzie.foundation.instruction;

public class LoadConstant {

    LoadConstant() {
    }

    public <Result extends Number> Instruction.Input<Result> of(Result value) {
        return visitor -> visitor.visitLdcInsn(value); // todo can't check numbers but don't know why
    }

    public Instruction.Input<Object> of(Object value) {
        return visitor -> visitor.visitLdcInsn(value);
    }

}
