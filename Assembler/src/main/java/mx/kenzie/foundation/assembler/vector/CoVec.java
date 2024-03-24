package mx.kenzie.foundation.assembler;

import org.valross.constantine.Constantive;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Covariant Vector.
 * A special implementation of an unsigned vector:
 * Rather than copying all of its data into a single byte array,
 * it preserves references to the original data source, making it much more memory efficient for reading from.
 */
public record CoVec(Data... data) implements UVec, Constantive {

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        for (Data datum : data) datum.write(stream);
    }

    @Override
    public int length() {
        int length = 0;
        for (Data datum : data) length += datum.length();
        return length;
    }

    @Override
    public ConVec constant() {
        return new UnsignedVector(this.binary());
    }

}
