package mx.kenzie.foundation.assembler;

import org.jetbrains.annotations.Contract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

public interface Data {

    @Contract(pure = true)
    int length();

    void write(OutputStream stream) throws IOException, ReflectiveOperationException;

    /**
     * Returns the data in a binary block.
     * This is stored in a byte array for convenience (rather than some unattributed off-heap memory address)
     * but the type is ARBITRARY -- the contents of the array are simply a jumble of bits.
     * Note: this may be the data itself (i.e. changes to the array might reflect in the vector) or it may be a copy.
     * For this reason it is unwise to mutate the array, since the behaviour will be undefined.
     *
     * @return The binary data, arbitrarily represented as bytes.
     */
    @Contract(pure = true)
    default byte[] binary() {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            this.write(stream);
            return stream.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

}
