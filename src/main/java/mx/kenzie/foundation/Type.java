package mx.kenzie.foundation;

import org.valross.constantine.RecordConstant;

import java.lang.constant.Constable;
import java.lang.invoke.TypeDescriptor;
import java.lang.ref.SoftReference;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public record Type(String getTypeName, String descriptorString, String internalName)
    implements java.lang.reflect.Type, Descriptor, Constable, TypeDescriptor, RecordConstant {

    private static final Map<Class<?>, SoftReference<Type>> CACHE = new WeakHashMap<>();

    public static Type of(String path, String name) {
        final String internal = path.replace('.', '/') + '/' + name;
        return new Type(path + '.' + name, 'L' + internal + ';', internal);
    }    public static final Type BYTE = Type.of(byte.class), SHORT = Type.of(short.class), INT = Type.of(int.class),
        LONG = Type.of(long.class), FLOAT = Type.of(float.class), DOUBLE = Type.of(double.class), BOOLEAN =
        Type.of(boolean.class), CHAR = Type.of(char.class), VOID = Type.of(void.class), OBJECT =
        Type.of(Object.class), STRING = Type.of(String.class);

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

    public static Type fromDescriptor(TypeDescriptor value) {
        if (value instanceof java.lang.reflect.Type type) return of(type);
        var string = value.descriptorString();
        string = string.substring(string.lastIndexOf(')') + 1);
        return of(string);
    }

    private static Type of(String descriptor) {
        descriptor = descriptor.substring(descriptor.lastIndexOf(')') + 1);
        return switch (descriptor) {
            case "Z" -> BOOLEAN;
            case "B" -> BYTE;
            case "S" -> SHORT;
            case "I" -> INT;
            case "J" -> LONG;
            case "F" -> FLOAT;
            case "D" -> DOUBLE;
            case "C" -> CHAR;
            case "V" -> VOID;
            default -> new Type(getTypeName(descriptor), descriptor, getInternalName(descriptor));
        };
    }

    public static Type[] parameters(TypeDescriptor value) {
        if (value instanceof java.lang.reflect.Type) return new Type[0];
        var string = value.descriptorString();
        if (!string.startsWith("(") || !string.contains(")") || string.charAt(1) == ')') return new Type[0];
        string = string.substring(1, string.lastIndexOf(')'));
        final int length = string.length();
        final List<Type> list = new ArrayList<>(8);
        for (int i = 0; i < length; i++) {
            switch (string.charAt(i)) {
                case 'L' -> {
                    final int end = string.indexOf(';', i);
                    final String part = string.substring(i, end + 1);
                    list.add(Type.of(part));
                    i = end;
                }
                case '[' -> {
                    eat:
                    for (int next = i; next < length; next++) {
                        switch (string.charAt(next)) {
                            case '[':
                                continue;
                            case 'L':
                                final int end = string.indexOf(';', next);
                                final String part = string.substring(i, end + 1);
                                list.add(Type.of(part));
                                i = end;
                                break eat;
                            default: // primitive array
                                final String us = string.substring(i, next + 1);
                                list.add(Type.of(us));
                                i = next;
                                break eat;
                        }
                    }
                }
                default -> list.add(Type.of(String.valueOf(string.charAt(i))));
            }
        }
        assert !list.isEmpty();
        return list.toArray(new Type[0]);
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

    private static String getTypeName(String string) {
        if (string.startsWith("[")) {
            int i = 0;
            for (; i < string.length(); i++) {
                if (string.charAt(i) != '[') break;
            }
            if (string.charAt(i) == 'L')
                return string.substring(i + 1, string.length() - 1).replace('/', '.') + "[]".repeat(i);
            else return toClass(string.substring(i), null).getTypeName() + "[]".repeat(i);
        }
        if (string.startsWith("L")) return string.substring(1, string.length() - 1).replace('/', '.');
        throw new IllegalArgumentException("Cannot accept primitives here: " + string);
    }

    private static String getInternalName(String string) {
        if (string.startsWith("[")) return string;
        if (string.startsWith("L")) return string.substring(1, string.length() - 1);
        return string;
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
    public static <Klass extends java.lang.reflect.Type & TypeDescriptor> String methodDescriptor(Klass result,
                                                                                                  Klass... parameters) {
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

    private static Class<?> toClass(String descriptorString, String getTypeName) {
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

    private static boolean isPrimitive(String descriptorString) {
        return switch (descriptorString) {
            case "B", "S", "I", "J", "F", "D", "C", "Z", "V" -> true;
            default -> false;
        };
    }

    public Class<?> toClass() {
        return toClass(descriptorString, getTypeName);
    }

    public boolean isPrimitive() {
        return isPrimitive(descriptorString);
    }

    public boolean isKnown() {
        return this.toClass() != null;
    }

    public boolean isKnownInterface() {
        final Class<?> type = this.toClass();
        if (type == null) return false;
        return type.isInterface();
    }

    @Override
    public Constable[] serial() {
        return new String[] {getTypeName, descriptorString, internalName};
    }

    @Override
    public Class<?>[] canonicalParameters() {
        return new Class[] {String.class, String.class, String.class};
    }

    public Type arrayType() {
        return new Type(getTypeName + "[]", '[' + descriptorString, '[' + descriptorString);
    }

    public Type componentType() {
        if (!descriptorString.startsWith("[") || !getTypeName.endsWith("[]"))
            throw new UnsupportedOperationException("Cannot get component type of non-array " + descriptorString);
        final String sub = descriptorString.substring(1);
        final String typeName = getTypeName.substring(0, getTypeName.length() - 2);
        if (sub.charAt(0) == 'L')
            return new Type(typeName, sub, sub.substring(0, sub.length() - 1));
        return new Type(typeName, sub, sub);
    }



}
