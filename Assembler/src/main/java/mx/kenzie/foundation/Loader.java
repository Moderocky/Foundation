package mx.kenzie.foundation;

public interface Loader {

    Loader DEFAULT = new SimpleClassLoader();

    static <Maker extends ClassLoader & Loader> Maker getDefault() {
        return (Maker) DEFAULT;
    }

    static <Maker extends ClassLoader & Loader> Maker createDefault() {
        return (Maker) new SimpleClassLoader();
    }

    Class<?> loadClass(String name, byte[] bytecode);

}

class SimpleClassLoader extends ClassLoader implements Loader {

    protected SimpleClassLoader() {
        this(Loader.class.getClassLoader());
    }

    protected SimpleClassLoader(ClassLoader parent) {
        super(parent);
    }

    SimpleClassLoader(Void unused) {
        super();
    }

    public Class<?> loadClass(String name, byte[] bytecode) {
        return super.defineClass(name, bytecode, 0, bytecode.length);
    }

}
