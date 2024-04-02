package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.detail.Erasure;
import mx.kenzie.foundation.detail.Type;

import java.lang.invoke.TypeDescriptor;

import static mx.kenzie.foundation.assembler.code.OpCode.*;

public class CallSuper {

    CallSuper() {
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass owner, Klass returnType, String name, Klass... parameters) {
        return new Stub(Type.of(owner), Type.of(returnType), name, Type.array(parameters));
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass owner, Klass... parameters) {
        return new Stub(Type.of(owner), Type.of(void.class), "<init>", Type.array(parameters));
    }

    public final <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass owner, Erasure erasure) {
        return new Stub(Type.of(owner), erasure.returnType(), erasure.name(), erasure.parameters());
    }

    public record Stub(Type owner, Type returnType, String name, Type... parameters) {

        public Instruction.Base call(Instruction.Input<Object> object, Instruction.Input<?>... arguments) {
            return builder -> {
                object.write(builder);
                for (Instruction.Input<?> argument : arguments) argument.write(builder);
                builder.write(INVOKESPECIAL.method(owner, returnType, name, parameters));
                if (returnType != Type.VOID) builder.write(POP);
            };
        }

        public <Result> Instruction.Input<Result> get(Instruction.Input<Object> object,
                                                      Instruction.Input<?>... arguments) {
            return builder -> {
                object.write(builder);
                for (Instruction.Input<?> argument : arguments) argument.write(builder);
                builder.write(INVOKESPECIAL.method(owner, returnType, name, parameters));
                if (returnType == Type.VOID) builder.write(ACONST_NULL);
            };
        }

    }

}
