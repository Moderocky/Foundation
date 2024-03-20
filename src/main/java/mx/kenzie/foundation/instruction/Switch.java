package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.HashSet;

public class Switch {

    public Lookup lookup(Instruction.Input<?> input) {
        return new Lookup(input);
    }

    public class Lookup {

        protected final Instruction.Input<?> source;
        protected final HashSet<Case> cases;
        protected Label alternative = new Label();
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
            return visitor -> {
                final int[] keys = new int[cases.size()];
                final Label[] labels = new Label[cases.size()];
                int index = 0;
                for (Case aCase : cases) {
                    keys[index] = aCase.match;
                    labels[index++] = aCase.label;
                }
                this.source.write(visitor);
                visitor.visitLookupSwitchInsn(alternative, keys, labels);
                for (Case aCase : cases) {
                    aCase.block.write(visitor);
                }
                if (closer != null) closer.write(visitor);
            };
        }

        public record Case(int match, Label label, SwitchBlock block) {

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
            public void write(MethodVisitor visitor) {
                visitor.visitLabel(start);
                for (Instruction instruction : instructions) instruction.write(visitor);
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
