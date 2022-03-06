package mx.kenzie.foundation;

import org.objectweb.asm.Opcodes;

import java.lang.invoke.TypeDescriptor;
import java.util.Arrays;

/**
 * Used as a placeholder for pre-compilation or otherwise missing classes.
 */
public record Type(String dotPath, String descriptor, String internalName)
    implements java.lang.reflect.Type, TypeDescriptor {
    
    public Type(String dotPath) {
        this(dotPath, "L" + dotPath.replace(".", "/") + ";", dotPath.replace(".", "/"));
    }
    
    public Type(Class<?> cls) {
        this(cls.getName(), cls.descriptorString(), org.objectweb.asm.Type.getInternalName(cls));
    }
    
    public <T extends java.lang.reflect.Type & TypeDescriptor> Type(T type) {
        this(type.getTypeName(), type.descriptorString(), getInternalName(type));
    }
    
    private static String getInternalName(final java.lang.reflect.Type type) {
        return type.getTypeName().replace('.', '/');
    }

    private static String getDescriptor(final String dotPath) {
        return org.objectweb.asm.Type.getType(switch (dotPath) {
            case "byte", "short", "int", "char", "boolean", "double", "float", "long", "void" -> dotPath;
            default -> "L" + dotPath.replace('.', '/') + ";";
        }).getDescriptor();
    }
    
    public static Type[] of(Class<?>... classes) {
        final Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; i++) {
            types[i] = new Type(classes[i]);
        }
        return types;
    }
    
    public static Type[] array(Class<?> type, int count) {
        final Type[] types = new Type[count];
        Arrays.fill(types, new Type(type));
        return types;
    }
    
    public static Type[] array(Type type, int count) {
        final Type[] types = new Type[count];
        Arrays.fill(types, type);
        return types;
    }
    
    public static Type of(final String internalName) {
        return new Type(internalName.replace("/", "."), "L" + internalName + ";", internalName);
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
    
    public String getSimpleName() {
        if (descriptor.startsWith("["))
            return internalName.substring(internalName.lastIndexOf('/') + 1, internalName.length() - 1) + arrayBlocks();
        else return internalName.substring(internalName.lastIndexOf('/') + 1);
    }
    
    private String arrayBlocks() {
        final StringBuilder builder = new StringBuilder();
        String input = descriptor;
        int x;
        while ((x = input.indexOf("[")) > -1) {
            input = input.substring(x + 1); // count array dimensions
            builder.append("[]"); // stack array dimensions
        }
        return builder.toString();
        
    }
    
    public boolean isPrimitiveArray() {
        if (!isArray()) return false;
        return descriptor.length() == 2 && !descriptor.endsWith(";");
    }
    
    public boolean isArray() {
        return descriptor.startsWith("[");
    }
    
    public int getDimensions() {
        return org.objectweb.asm.Type.getType(descriptor).getDimensions();
    }
    
    public Type arrayType() {
        return new Type("[" + descriptor, "[" + descriptor, "[" + descriptor);
    }
    
    public Type componentType() {
        if (this.descriptor.startsWith("[")) {
            final String string = descriptor.substring(1);
            final org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(string);
            return new Type(type.getClassName(), string, type.getInternalName());
        } else return this;
    }
    
    @Override
    public String getTypeName() {
        return dotPath;
    }
    
    @Override
    public String descriptorString() {
        return descriptor;
    }
    
    int getLoadOpcode() {
        return switch (dotPath) {
            case "byte", "short", "int", "char", "boolean" -> Opcodes.ILOAD;
            case "double" -> Opcodes.DLOAD;
            case "float" -> Opcodes.FLOAD;
            case "long" -> Opcodes.LLOAD;
            default -> Opcodes.ALOAD;
        };
    }
    
    int getStoreOpcode() {
        return switch (dotPath) {
            case "byte", "short", "int", "char", "boolean" -> Opcodes.ISTORE;
            case "double" -> Opcodes.DSTORE;
            case "float" -> Opcodes.FSTORE;
            case "long" -> Opcodes.LSTORE;
            default -> Opcodes.ASTORE;
        };
    }
    
    public boolean matches(final Class<?> type) {
        return type.getName().equals(dotPath);
    }
    
    public boolean isPrimitive() {
        return switch (dotPath) {
            case "byte", "short", "int", "long", "float", "double", "char", "boolean", "void" -> true;
            default -> false;
        };
    }
    
    public int words() {
        return switch (dotPath) {
            case "byte", "short", "int", "char", "boolean", "void" -> 1;
            default -> 2;
        };
    }
    
    public Class<?> findClass() {
        try {
            return Class.forName(dotPath);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
}
