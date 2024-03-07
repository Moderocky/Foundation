package mx.kenzie.foundation;

import java.lang.invoke.TypeDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;

public record Type(String getTypeName, String descriptorString, String internalName)
    implements java.lang.reflect.Type, TypeDescriptor {

    private static final Map<Class<?>, SoftReference<Type>> CACHE = new WeakHashMap<>();
    public static final Type
        BYTE = Type.of(byte.class),
        SHORT = Type.of(short.class),
        INT = Type.of(int.class),
        LONG = Type.of(long.class),
        FLOAT = Type.of(float.class),
        DOUBLE = Type.of(double.class),
        BOOLEAN = Type.of(boolean.class),
        CHAR = Type.of(char.class),
        VOID = Type.of(void.class),
        OBJECT = Type.of(Object.class),
        STRING = Type.of(String.class);

    public static Type of(String path, String name) {
        final String internal = path.replace('.', '/') + '/' + name;
        return new Type(path + '.' + name, 'L' + internal + ';', internal);
    }

    @SafeVarargs
    public static <Klass extends java.lang.reflect.Type & TypeDescriptor> Type[] array(Klass... values) {
        final Type[] types = new Type[values.length];
        for (int i = 0; i < values.length; i++) {
            final Klass value = values[i];
            if (value instanceof Type type) types[i] = type;
            else types[i] = Type.of(value);
        }
        return types;
    }

    public static Type[] array(Parameter... values) {
        final Type[] types = new Type[values.length];
        for (int i = 0; i < values.length; i++) types[i] = Type.of(values[i].getType());
        return types;
    }

    public static Type of(java.lang.reflect.Type value) {
        if (value instanceof Type type) return type;
        if (value instanceof Class<?> thing) {
            final Type result;
            final SoftReference<Type> reference = CACHE.get(thing);
            if (reference != null) {
                final Type check = reference.get();
                if (check != null) return check;
            }
            result = new Type(value.getTypeName(), thing.descriptorString(), Type.internalName(value));
            CACHE.put(thing, new SoftReference<>(result));
            return result;
        } else return new Type(value.getTypeName(), Type.descriptorString(value), Type.internalName(value));
    }

    private static Type of(Class<?> value) {
        final Type type = new Type(value.getTypeName(), value.descriptorString(), Type.internalName(value));
        CACHE.put(value, new SoftReference<>(type));
        return type;
    }

    public static String internalName(java.lang.reflect.Type klass) {
        if (klass instanceof Class<?> thing) return thing.getName().replace('.', '/');
        if (klass instanceof Type type) return type.internalName;
        if (isPrimitive(klass)) return ofPrimitive(klass).internalName();
        return klass.getTypeName().replaceAll("<.+>", "").replace('.', '/');
    }

    public static String descriptorString(java.lang.reflect.Type klass) {
        if (klass instanceof TypeDescriptor descriptor) return descriptor.descriptorString();
        if (isPrimitive(klass)) return ofPrimitive(klass).descriptorString();
        return 'L' + internalName(klass) + ';';
    }

    private static boolean isPrimitive(java.lang.reflect.Type type) {
        if (type instanceof Class<?> cls) return cls.isPrimitive();
        else if (type instanceof TypeDescriptor descriptor) return isPrimitive(descriptor.descriptorString());
        return switch (type.getTypeName()) {
            case "int", "long", "short", "boolean", "void", "float", "double", "byte", "char" -> true;
            default -> false;
        };
    }

    private static Type ofPrimitive(java.lang.reflect.Type unknown) {
        return switch (unknown.getTypeName()) {
            case "boolean" -> BOOLEAN;
            case "byte" -> BYTE;
            case "short" -> SHORT;
            case "int" -> INT;
            case "long" -> LONG;
            case "float" -> FLOAT;
            case "double" -> DOUBLE;
            case "char" -> CHAR;
            case "void" -> VOID;
            default -> OBJECT;
        };
    }

    @SafeVarargs
    public static <Klass extends java.lang.reflect.Type & TypeDescriptor>
    String methodDescriptor(Klass result, Klass... parameters) {
        final StringBuilder builder = new StringBuilder("(");
        for (Klass parameter : parameters) builder.append(parameter.descriptorString());
        builder.append(')').append(result.descriptorString());
        return builder.toString();
    }

    public static Class<?>[] classArray(Type... values) {
        final Class<?>[] classes = new Class[values.length];
        for (int i = 0; i < values.length; i++) classes[i] = values[i].toClass();
        return classes;
    }

    public Class<?> toClass() {
        return switch (descriptorString) {
            case "B" -> byte.class;
            case "S" -> short.class;
            case "I" -> int.class;
            case "J" -> long.class;
            case "F" -> float.class;
            case "D" -> double.class;
            case "C" -> char.class;
            case "Z" -> boolean.class;
            case "V" -> void.class;
            default -> {
                try {
                    yield Class.forName(getTypeName);
                } catch (ClassNotFoundException e) {
                    yield null;
                }
            }
        };
    }

    public boolean isPrimitive() {
        return isPrimitive(descriptorString);
    }

    private static boolean isPrimitive(String descriptorString) {
        return switch (descriptorString) {
            case "B", "S", "I", "J", "F", "D", "C", "Z", "V" -> true;
            default -> false;
        };
    }

    public boolean isKnown() {
        return this.toClass() != null;
    }

    public boolean isKnownInterface() {
        final Class<?> type = this.toClass();
        if (type == null) return false;
        return type.isInterface();
    }

}
