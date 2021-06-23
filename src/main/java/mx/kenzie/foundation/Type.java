package mx.kenzie.foundation;

import java.lang.invoke.TypeDescriptor;

/**
 * Used as a placeholder for pre-compilation or otherwise missing classes.
 */
public record Type(String dotPath, String descriptor, String internalName)
    implements java.lang.reflect.Type, TypeDescriptor {
    
    public Type(String dotPath) {
        this(dotPath, dotPath.replace(".", "/"), "L" + dotPath.replace(".", "/") + ";");
    }
    
    public Type(Class<?> cls) {
        this(cls.getName(), cls.descriptorString(), org.objectweb.asm.Type.getInternalName(cls));
    }
    
    public <T extends java.lang.reflect.Type & TypeDescriptor> Type(T type) {
        this(type.getTypeName(), type.descriptorString(), getInternalName(type));
    }
    
    @Override
    public String getTypeName() {
        return dotPath;
    }
    
    @Override
    public String descriptorString() {
        return descriptor;
    }
    
    public static Type[] of(Class<?>... classes) {
        final Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; i++) {
            types[i] = new Type(classes[i]);
        }
        return types;
    }
    
    private static String getInternalName(final Class<?> cls) {
        assert !cls.isArray();
        if (cls.isHidden()) {
            String name = cls.getName();
            int index = name.indexOf('/');
            return name.substring(0, index).replace('.', '/')
                + "." + name.substring(index + 1);
        } else {
            return cls.getName().replace('.', '/');
        }
    }
    
    private static String getInternalName(final java.lang.reflect.Type type) {
        return type.getTypeName().replace('.', '/');
    }
    
}
