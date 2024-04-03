package mx.kenzie.foundation;

import mx.kenzie.foundation.assembler.code.Branch;
import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.instruction.Instruction;
import mx.kenzie.foundation.instruction.Return;

import java.util.LinkedList;
import java.util.List;

import static mx.kenzie.foundation.assembler.code.OpCode.GOTO;
import static mx.kenzie.foundation.assembler.code.OpCode.IFEQ;

public class Block implements CodeBody, Instruction.Base {

    public final Branch start = new Branch(), end = new Branch();
    protected List<Instruction> instructions = new LinkedList<>();

    public void line(Instruction.Block instruction) {
        this.line(builder -> instruction.write(builder, this));
    }

    @Override
    public void line(Instruction.Base instruction) {
        this.instructions.add(instruction);
    }

    @Override
    public Instruction[] lines() {
        return instructions.toArray(new Instruction[0]);
    }

    @Override
    public void write(CodeBuilder builder) {
        builder.write(start);
        for (Instruction instruction : instructions) instruction.write(builder);
        builder.write(end);
    }

    public static class If extends mx.kenzie.foundation.Block {

        protected Instruction.Input<Integer> condition;
        protected mx.kenzie.foundation.Block elseBlock;

        public If(Instruction.Input<Integer> condition) {
            this.condition = condition;
        }

        public mx.kenzie.foundation.Block elseBlock() {
            return elseBlock != null ? elseBlock : (elseBlock = new mx.kenzie.foundation.Block());
        }

        @Override
        public void write(CodeBuilder builder) {
            if (elseBlock == null) {
                this.condition.write(builder);
                builder.write(IFEQ.jump(end));
                for (Instruction instruction : instructions) instruction.write(builder);
                builder.write(end);
            } else {
                this.condition.write(builder);
                builder.write(IFEQ.jump(elseBlock.start));
                for (Instruction instruction : instructions) instruction.write(builder);
                if (!(instructions.getLast() instanceof Return.ReturnInstruction))
                    builder.write(GOTO.jump(elseBlock.end));
                this.elseBlock.write(builder);
            }
        }

    }

}
