package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.attribute.frame.StackMapFrame;
import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.tool.ProgramStack;
import mx.kenzie.foundation.detail.TypeHint;
import org.valross.constantine.Constantive;
import org.valross.constantine.RecordConstant;

public record Frame(ClassFileBuilder.Storage storage, Branch branch, Branch previous)
    implements Constantive {

    private int index() {
        return branch.handle.index();
    }

    private int lastIndex() {
        return previous.handle.index();
    }

    private int offset() {
        if (previous instanceof Branch.ImplicitBranch) return branch.getHandle().index();
        else return this.index() - this.lastIndex() - 1;
    }

    @Override
    public StackMapFrame constant() {
        return StackMapFrame.of(storage, this.offset(), previous.toStackMap(),
                                this.branch.toStackMap());
    }

    /**
     * An un-used enum for determining the frame type from its index.
     */
    public enum FrameType {

        /**
         * Everything is the same as it was in the previous frame. The stack is EMPTY.
         */
        SAME_FRAME(1),
        /**
         * The variables are the same as the previous frame, and there is ONE item on the stack.
         * The item is detailed.
         */
        SAME_LOCALS_1_STACK_ITEM_FRAME(-1),
        /**
         * The variables are the same as the previous frame, and there is ONE item on the stack.
         * The item is detailed.
         * The offset from the previous frame is detailed.
         */
        SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED(-1),
        /**
         * The variables are the same as the previous frame, but the last N slots are removed. The stack is EMPTY.
         * The offset from the previous frame is detailed.
         */
        CHOP_FRAME(3),
        /**
         * Everything is the same as it was in the previous frame. The stack is EMPTY.
         * The offset from the previous frame is detailed.
         */
        SAME_FRAME_EXTENDED(3),
        /**
         * The variables are the same as the previous frame, with an extra N slots. The stack is EMPTY.
         * The offset from the previous frame is detailed.
         */
        APPEND_FRAME(-1),
        /**
         * The whole frame (variable and stack) information is detailed.
         */
        FULL_FRAME(-1);
        public final int length;

        FrameType(int length) {
            this.length = length;
        }

        public static FrameType valueOf(byte index) {
            final int i = Byte.toUnsignedInt(index);
            if (i == 255) return FULL_FRAME;
            if (i == 251) return SAME_FRAME_EXTENDED;
            if (i == 247) return SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED;
            if (i >= 252) return APPEND_FRAME;
            if (i >= 248) return CHOP_FRAME;
            if (i >= 64) return SAME_LOCALS_1_STACK_ITEM_FRAME;
            return SAME_FRAME;
        }

        public boolean hasFixedLength() {
            return length != -1;
        }

        public int length() {
            return length;
        }

    }

    public record Map(TypeHint[] register, TypeHint... stack)
        implements RecordConstant {

        public Map(ProgramStack register, ProgramStack stack) {
            this(register.toWideArray(), stack.toWideArray());
        }

        public int registerSize() {
            int slots = 0;
            for (TypeHint type : register) if (type != null) slots += type.width();
            return slots;
        }

        public int stackSize() {
            int slots = 0;
            for (TypeHint type : stack) if (type != null) slots += type.width();
            return slots;
        }

    }

}
