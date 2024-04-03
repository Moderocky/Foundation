package org.valross.foundation.assembler.tool;

import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.UVec;
import org.jetbrains.annotations.NotNull;
import org.valross.constantine.Constantive;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.util.Objects;

public abstract class TableReference<Element> implements UVec, Data, Constantive {

    protected final Iterable<Element> pool;
    protected final Reference<Element> reference;

    protected TableReference(Iterable<Element> pool, Reference<Element> reference) {
        this.pool = pool;
        this.reference = reference;
    }

    public abstract int index();

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
        return new byte[] {(byte) (value >>> 8), (byte) (value)};
    }

    public Element get() {
        return reference.get();
    }

    public @NotNull Element ensure() {
        return Objects.requireNonNull(this.get());
    }

    /**
     * Retracts a use of this entry. If there are no remaining uses of the entry,
     * the entry may be purged from its table (subject to reference implementation).
     */
    public void purge() {
        // Todo
    }

}
