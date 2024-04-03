package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.lang.invoke.TypeDescriptor;
import java.util.Iterator;

import static org.valross.foundation.assembler.code.OpCode.*;

public class ForEach {

    ForEach() {
    }

    public Block loop(Instruction.Base before, Instruction.Input<? extends Number> check, Instruction.Base after) {
        return new Block() {
            @Override
            public void write(CodeBuilder builder) {
                before.write(builder);
                builder.write(start);
                check.write(builder);
                builder.write(IFEQ.jump(end));
                for (Instruction instruction : instructions) instruction.write(builder);
                after.write(builder);
                builder.write(GOTO.jump(start), end);
            }
        };
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Block forEach(int var, Instruction.Input<Object> iterator, Klass expected) {
        return new Block() {
            @Override
            public void write(CodeBuilder builder) {
                builder.write(ACONST_NULL, ASTORE.var(var), start);
                iterator.write(builder);
                builder.write(DUP, INVOKEINTERFACE.interfaceMethod(Iterator.class, boolean.class, "hasNext"));
                builder.write(IFEQ.jump(end), INVOKEINTERFACE.interfaceMethod(Iterator.class, Object.class, "next"));
                builder.write(CHECKCAST.type(expected), ASTORE.var(var));
                for (Instruction instruction : instructions) instruction.write(builder);
                builder.write(GOTO.jump(start), end);
            }
        };
    }

}
