package mx.kenzie.foundation;

import java.lang.invoke.TypeDescriptor;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;

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

    public static <Klass extends java.lang.reflect.Type & TypeDescriptor> Type of(Klass value) {
        if (value instanceof Type type) return type;
        if (!(value instanceof Class<?> thing))
            return new Type(value.getTypeName(), value.descriptorString(), Type.internalName(value));
        final Type result;
        final SoftReference<Type> reference = CACHE.get(thing);
        if (reference != null) {
            final Type check = reference.get();
            if (check != null) return check;
        }
        result = new Type(value.getTypeName(), value.descriptorString(), Type.internalName(value));
        CACHE.put(thing, new SoftReference<>(result));
        return result;
    }

    public static <Klass extends java.lang.reflect.Type & TypeDescriptor> String internalName(Klass klass) {
        if (klass instanceof Class<?> thing) return thing.getName().replace('.', '/');
        if (klass instanceof Type type) return type.internalName;
        return klass.getTypeName().replace('.', '/');
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
        return switch (descriptorString) {
            case "B", "S", "I", "J", "F", "D", "C", "Z", "V" -> true;
            default -> false;
        };
    }
}
