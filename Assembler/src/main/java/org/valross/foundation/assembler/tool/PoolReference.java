package org.valross.foundation.assembler.tool;

import org.valross.foundation.assembler.constant.ConstantPoolInfo;
import org.valross.foundation.assembler.constant.DeadSpaceInfo;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class PoolReference extends TableReference<ConstantPoolInfo> {

    public static final PoolReference ZERO = new Zero();

    public PoolReference(Iterable<ConstantPoolInfo> pool, ConstantPoolInfo value) {
        super(pool, new WeakReference<>(value));
    }

    @Override
    public int index() {
        final ConstantPoolInfo info = reference.get();
        if (info == null) return -1; // it was discarded :(
        int index = 1;
        for (ConstantPoolInfo value : pool) {
            if (info == value) return index; // we want to know if it's EXACTLY our reference
            index += value.tag().indices();
        }
        return -1; // was it ever there in the first place?
    }

    @Override
    public String toString() {
        return "PoolReference[" +
            "index=" + this.index() +
            ", reference=" + reference.get() +
            ']';
    }

    static final class Zero extends PoolReference {

        public Zero() {
            super(null, null);
        }

        @Override
        public int index() {
            return 0;
        }

        @Override
        public ConstantPoolInfo get() {
            return new DeadSpaceInfo();
        }

        @Override
        public @NotNull ConstantPoolInfo ensure() {
            return new DeadSpaceInfo();
        }

    }

}
