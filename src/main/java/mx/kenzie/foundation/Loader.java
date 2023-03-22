package mx.kenzie.foundation;

public interface Loader {
    Loader DEFAULT = new SimpleClassLoader();

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
