package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.assembler.attribute.StackMapTable;
import mx.kenzie.foundation.assembler.attribute.frame.StackMapFrame;
import mx.kenzie.foundation.assembler.code.Branch;
import mx.kenzie.foundation.assembler.code.Frame;

import java.util.ArrayList;
import java.util.List;

public class StackMapTableBuilder implements AttributeBuilder {

    protected final List<Frame> frames;
    private final ClassFileBuilder.Storage storage;
    private StackMapTable table;

    protected StackMapTableBuilder(ClassFileBuilder.Storage storage) {
        this.storage = storage;
        this.frames = new ArrayList<>();
    }

    public boolean isUsed() {
        return !frames.isEmpty();
    }

    @Override
    public StackMapTable build() {
        if (table == null) this.finalise();
        return table;
    }

    @Override
    public void finalise() {
        final List<StackMapFrame> list = new ArrayList<>();
        for (Frame frame : frames) list.add(frame.constant());
        this.table = new StackMapTable(storage, list.toArray(new StackMapFrame[0]));
    }

    public void addFrame(ClassFileBuilder.Storage storage, Branch previous, Branch current) {
        this.frames.add(new Frame(storage, current, previous));
    }

}
