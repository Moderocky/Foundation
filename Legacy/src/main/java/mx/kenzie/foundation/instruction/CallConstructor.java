package mx.kenzie.foundation.instruction;

import org.valross.foundation.detail.Type;

import java.lang.invoke.TypeDescriptor;

import static org.valross.foundation.assembler.code.OpCode.*;

public class CallConstructor {

    CallConstructor() {
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass type, Klass... parameters) {
        return new Stub(Type.of(type), Type.array(parameters));
    }

    public record Stub(Type owner, Type... parameters) {

        public Instruction.Input<Object> make(Instruction.Input<?>... arguments) {
            return builder -> {
                builder.write(NEW.type(owner), DUP);
                for (Instruction.Input<?> argument : arguments) argument.write(builder);
                builder.write(INVOKESPECIAL.constructor(owner, parameters));
            };

        }

    }

}
