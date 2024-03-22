package mx.kenzie.foundation;

import org.junit.AfterClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class FoundationTest {

    static FoundationTest test;
    protected final PreClass thing;

    {
        test = this;
    }

    public FoundationTest() {
        this.thing = new PreClass("org.example", this.getClass().getSimpleName() + "Result");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.dump(thing)));
    }

    @AfterClass
    public static void finish() throws Exception {
        final Class<?> loaded = test.thing.load(Loader.DEFAULT);
        for (Method method : loaded.getDeclaredMethods()) {
            if (method.getReturnType() != boolean.class) continue;
            if (!Modifier.isStatic(method.getModifiers())) continue;
            if (!Modifier.isPublic(method.getModifiers())) continue;
            if (method.getParameterCount() > 0) continue;
            final boolean result = (boolean) method.invoke(null);
            assert result : method.getName();
        }
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
