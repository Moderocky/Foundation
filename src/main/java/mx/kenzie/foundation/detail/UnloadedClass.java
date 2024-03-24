package mx.kenzie.foundation.detail;

import mx.kenzie.foundation.Loader;
import mx.kenzie.foundation.Type;
import org.valross.constantine.RecordConstant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public record UnloadedClass(Type type, byte[] bytecode) implements RecordConstant, Descriptor, java.lang.reflect.Type {

    public UnloadedClass(Class<?> loaded) {
        this(Type.of(loaded), UnloadedClass.getBytecode(loaded));
    }

    public UnloadedClass(String path, File file) throws IOException {
        this(Type.of(path, file.getName()
            .substring(0, file.getName().length() - 6)), Files.readAllBytes(file.toPath()));
    }

    private static byte[] getBytecode(Class<?> loaded) {
        try (final InputStream stream = ClassLoader.getSystemResourceAsStream(loaded.getName()
            .replace('.', '/') + ".class")) {
            assert stream != null;
            return stream.readAllBytes();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void write(File file)
        throws IOException {
        try (final OutputStream stream = Files.newOutputStream(file.toPath())) {
            stream.write(bytecode);
        }
    }

    public Class<?> load(Loader loader) {
        return loader.loadClass(type.getTypeName(), bytecode);
    }

    @Override
    public String descriptorString() {
        return type.descriptorString();
    }

    @Override
    public String getTypeName() {
        return type.getTypeName();
    }

}
