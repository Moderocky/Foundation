package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.U2;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.constant.ConstantPoolInfo;
import org.jetbrains.annotations.NotNull;
import org.valross.constantine.Constantive;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class PoolReference
    implements UVec, Data, Constantive {

    private final Iterable<ConstantPoolInfo> pool;
    private final Reference<ConstantPoolInfo> reference;

    public PoolReference(Iterable<ConstantPoolInfo> pool, ConstantPoolInfo value) {
        this.pool = pool;
        this.reference = new WeakReference<>(value);
    }

    public int index() {
        final ConstantPoolInfo info = reference.get();
        if (info == null) return -1; // it was discarded :(
        int index = 0;
        for (ConstantPoolInfo value : pool) {
            if (info == value) return index; // we want to know if it's EXACTLY our reference
            index += value.tag().indices();
        }
        return -1; // was it ever there in the first place?
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        final short value = (short) this.index();
        stream.write((value >>> 8));
        stream.write(value);
    }

    @Override
    public U2 constant() {
        return U2.valueOf(this.index());
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public byte[] binary() {
        final short value = (short) this.index();
        return new byte[] {
            (byte) (value >>> 8),
            (byte) (value)
        };
    }

    public ConstantPoolInfo get() {
        return reference.get();
    }

    public @NotNull ConstantPoolInfo ensure() {
        return Objects.requireNonNull(this.get());
    }

}
