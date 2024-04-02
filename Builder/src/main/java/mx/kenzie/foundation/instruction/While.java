package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import mx.kenzie.foundation.assembler.tool.CodeBuilder;

import static mx.kenzie.foundation.assembler.code.OpCode.*;

public class While {

    While() {
    }

    public Block check(Instruction.Input<Integer> condition) {
        return new Block() {
            @Override
            public void write(CodeBuilder builder) {
                builder.write(start);
                condition.write(builder);
                builder.write(IFEQ.jump(end));
                for (Instruction instruction : instructions) instruction.write(builder);
                builder.write(GOTO.jump(start), end);
            }
        };
    }

    public Block doWhile(Instruction.Input<Integer> condition) {
        return new Block() {
            @Override
            public void write(CodeBuilder builder) {
                builder.write(start);
                for (Instruction instruction : instructions) instruction.write(builder);
                condition.write(builder);
                builder.write(IFNE.jump(start), end);
            }
        };
    }

}
