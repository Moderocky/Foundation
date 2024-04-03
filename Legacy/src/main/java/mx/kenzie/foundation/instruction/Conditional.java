package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import org.valross.foundation.assembler.tool.CodeBuilder;

import static org.valross.foundation.assembler.code.OpCode.*;

public class Conditional {

    Conditional() {
    }

    public Block.If check(Instruction.Input<Integer> condition) {
        return new Block.If(condition);
    }

    public Block compareInts(Instruction.Input<Integer> a, Instruction.Operator operator,
                             Instruction.Input<Integer> b) {
        return new Block() {
            @Override
            public void write(CodeBuilder builder) {
                builder.write(start);
                a.write(builder);
                b.write(builder);
                switch (operator) {
                    case OR -> builder.write(IOR);
                    case AND -> builder.write(IAND);
                    case XOR -> builder.write(IXOR);
                }
                switch (operator) {
                    case LESS -> builder.write(IF_ICMPGE.jump(end));
                    case GREATER -> builder.write(IF_ICMPLE.jump(end));
                    case LESS_EQ -> builder.write(IF_ICMPGT.jump(end));
                    case GREATER_EQ -> builder.write(IF_ICMPLT.jump(end));
                    case EQ -> builder.write(IF_ICMPNE.jump(end));
                    case NOT_EQ -> builder.write(IF_ICMPEQ.jump(end));
                    case OR, AND, XOR -> builder.write(IFEQ.jump(end));
                }
                for (Instruction instruction : instructions) instruction.write(builder);
                builder.write(end);
            }
        };
    }

}
