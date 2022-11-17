package mx.kenzie.foundation;

import java.io.IOException;
import java.io.InputStream;

public record UnloadedClass(Type type, byte[] bytecode) {
    
    public UnloadedClass(Class<?> loaded) {
        this(Type.of(loaded), UnloadedClass.getBytecode(loaded));
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
    
    public Class<?> load(Loader loader) {
        return loader.loadClass(type.getTypeName(), bytecode);
    }
    
}
