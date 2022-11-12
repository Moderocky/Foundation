package mx.kenzie.foundation;

import org.junit.AfterClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public abstract class FoundationTest {
    static FoundationTest test;
    protected final PreClass thing;
    protected final List<String> tests = new LinkedList<>();

    {
        test = this;
    }
    
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
    
    @AfterClass
    public static void finish() throws Exception {
        if (test.tests.size() > 0) {
            final Class<?> loaded = test.thing.load(Loader.DEFAULT);
            for (String test : test.tests) {
                final Method method = loaded.getDeclaredMethod(test);
                final boolean result = (boolean) method.invoke(null);
                assert result : test;
            }
        }
    }
    
}
