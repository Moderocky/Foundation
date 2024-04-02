package mx.kenzie.foundation.instruction;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Type;

import static mx.kenzie.foundation.assembler.code.OpCode.CHECKCAST;

public class Cast {

    Cast() {
    }

    public <Result extends Number, Input extends Number> Instruction.Input<Result> number(Instruction.Input<Input> value, Instruction.Convert convert) {
        return builder -> {
            value.write(builder);
            builder.write(convert.opcode);
        };
    }

    public <Klass extends Type & TypeDescriptor> Instruction.Input<Object> object(Instruction.Input<Object> value,
                                                                                  Klass type) {
        return builder -> {
            value.write(builder);
            builder.write(CHECKCAST.type(type));
        };
    }

}
