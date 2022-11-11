package mx.kenzie.foundation;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class FoundationTest extends TestCase {
    
    protected final PreClass thing;
    
    public FoundationTest() {
        this.thing = new PreClass("org.example", this.getClass().getSimpleName() + "Result");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.dump(thing)));
    }
    
    public void dump(PreClass thing) {
        try {
            final File file = new File("target/test-generation/" + this.getClass()
                .getSimpleName() + "/" + thing.type.getTypeName() + ".class");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            try (final OutputStream stream = new FileOutputStream(file)) {
                stream.write(thing.bytecode());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
