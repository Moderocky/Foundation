package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import org.valross.foundation.assembler.code.Branch;
import org.valross.foundation.assembler.code.LookupSwitchCode;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.util.Collection;
import java.util.HashSet;

import static org.valross.foundation.assembler.code.OpCode.LOOKUPSWITCH;

public class Switch {

    public Lookup lookup(Instruction.Input<?> input) {
        return new Lookup(input);
    }

    public class Lookup {

        protected final Instruction.Input<?> source;
        protected final Collection<Case> cases;
        protected Branch alternative = new Branch();
        protected SwitchBlock closer;

        public Lookup(Instruction.Input<?> source) {
            this.source = source;
            this.cases = new HashSet<>();
        }

        public SwitchBlock matchCase(int match) {
            final SwitchBlock block = new SwitchBlock();
            final Case c = new Case(match, block.start, block);
            block.handle = c;
            this.cases.add(c);
            return block;
        }

        public SwitchBlock defaultCase() {
            final SwitchBlock block = closer = new SwitchBlock();
            this.alternative = block.start;
            block.handle = new Case(0, block.start, block);
            return block;
        }

        public Instruction.Base close() {
            return builder -> {
                this.source.write(builder);
                final LookupSwitchCode.Builder test = LOOKUPSWITCH.test(alternative);
                builder.write(test);
                for (Case aCase : cases) {
                    test.test(aCase.match, aCase.branch);
                    aCase.block.write(builder);
                }
                if (closer != null) closer.write(builder);
            };
        }

        public record Case(int match, Branch branch, SwitchBlock block) {

            @Override
            public boolean equals(Object obj) {
                return this == obj || (obj instanceof Case other && other.match == match);
            }

            @Override
            public int hashCode() {
                return match;
            }

        }

        public class SwitchBlock extends Block {

            protected Lookup.Case handle;

            @Override
            public void write(CodeBuilder builder) {
                builder.write(start);
                for (Instruction instruction : instructions) instruction.write(builder);
            }

            public Lookup close() {
                return Lookup.this;
            }

        }

    }

    public class Table {

        protected final Instruction.Input<?> source;

        public Table(Instruction.Input<?> source) {
            this.source = source;
        }

    }

}
