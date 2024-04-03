package mx.kenzie.foundation.instruction;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Type;

import static org.valross.foundation.assembler.code.OpCode.*;

public class Array {

    Array() {
    }

    @SafeVarargs
    public final <Klass extends Type & TypeDescriptor> Instruction.Input<Object> of(Klass type,
                                                                                    Instruction.Input<Object>... values) {
        final org.valross.foundation.detail.Type found = org.valross.foundation.detail.Type.of(type);
        final int length = values.length;
        return builder -> {
            builder.write(SIPUSH.value(length), ANEWARRAY.type(found));
            for (int i = 0; i < length; i++) {
                builder.write(DUP, SIPUSH.value(i));
                values[i].write(builder);
                builder.write(AASTORE);
            }
        };
    }

}
