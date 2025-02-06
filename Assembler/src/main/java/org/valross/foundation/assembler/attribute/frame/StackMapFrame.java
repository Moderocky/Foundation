package org.valross.foundation.assembler.attribute.frame;

import org.valross.constantine.Constant;
import org.valross.foundation.assembler.code.Frame;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.vector.U1;
import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public interface StackMapFrame extends UVec, Constant {

    static StackMapFrame of(ClassFileBuilder.Storage storage, int offset, Frame.Map previous, Frame.Map ours) {
        if (offset > 63)
            throw new Error("Branches are too far apart, abnormal usage.");
        final int stackSize = ours.stackSize();
        if (stackSize == 0) {
            if (Arrays.equals(ours.register(), previous.register())) return new SameFrame(offset);
            final int ourSize = ours.registerSize(), theirSize = previous.registerSize(), difference =
                ourSize - theirSize;
            if (ourSize != theirSize && difference > -4 && difference < 4) {
                final int ourLength = ours.register().length;
                final int total = Math.min(ourLength, previous.register().length);
                if (Arrays.equals(ours.register(), 0, total, previous.register(), 0, total)) {
                    if (difference < 0)
                        return new ShiftFrame(offset, difference);
                    else
                        return new ShiftFrame(offset, difference, VerificationTypeInfo.of(storage,
                            Arrays.copyOfRange(ours.register(), ourLength - difference, ourLength)));
                }
            }
        } else if (stackSize == 1 && Arrays.equals(ours.register(), previous.register()))
            return new SameLocalsOneStackFrame(offset, VerificationTypeInfo.of(storage, ours.stack()[0]));
        return new FullFrame(offset, // todo which way around is the register?
            VerificationTypeInfo.of(storage, ours.register()),
            VerificationTypeInfo.of(storage, ours.stack()));
//        return new FullFrame(offset,
//            VerificationTypeInfo.of(storage, ours.stack()),
//            VerificationTypeInfo.of(storage, ours.register()));
    }

    U1 frame_type();

    UVec info();

    default @Override int length() {
        return 1 + this.info().length();
    }

    @Override
    default void write(OutputStream stream) throws IOException {
        this.frame_type().write(stream);
        this.info().write(stream);
    }

    default Frame.FrameType getType() {
        return Frame.FrameType.valueOf(this.frame_type().byteValue());
    }

    int offset();

}
