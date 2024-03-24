package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.jetbrains.annotations.NotNull;
import org.valross.constantine.Constantive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public abstract class TableReference<Element> implements UVec, Data, Constantive {

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

    public abstract Element get();

    public @NotNull Element ensure() {
        return Objects.requireNonNull(this.get());
    }

}
