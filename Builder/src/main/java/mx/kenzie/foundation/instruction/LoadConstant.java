package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.assembler.code.OpCode;

import java.lang.constant.Constable;

public class LoadConstant {

    LoadConstant() {
    }

    public <Result extends Number & Constable> Instruction.Input<Result> of(Result value) {
        return builder -> builder.write(OpCode.LDC.value(value)); // todo does this need to be separate?
    }

    public Instruction.Input<Object> of(Constable value) {
        return builder -> builder.write(OpCode.LDC.value(value));
    }

}
