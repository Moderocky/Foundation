package org.valross.foundation.detail;

import org.jetbrains.annotations.NotNull;
import org.valross.constantine.Canonical;
import org.valross.constantine.RecordConstant;

import java.lang.constant.ClassDesc;
import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.invoke.TypeDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;

public record Type(String getTypeName, String descriptorString, String internalName)
    implements java.lang.reflect.Type, TypeHint, Descriptor, Constable, TypeDescriptor, RecordConstant,
    Canonical<Type> {

    private static final Map<Class<?>, SoftReference<Type>> classCache = new WeakHashMap<>();
    private static final Map<String, SoftReference<Type>> descriptorCache = new WeakHashMap<>();

    public static Type of(String path, String name) {
        final String internal = path.replace('.', '/') + '/' + name, descriptor = 'L' + internal + ';';
        return getCached(descriptorCache, descriptor, _ -> new Type(path + '.' + name, descriptor, internal));
    }

    public static Type of(ClassDesc description) {
        return fromDescriptor(description);
    }

    public static Type valueOf(String getTypeName, String descriptorString, String internalName) {
        return getCached(descriptorCache, descriptorString, _ -> new Type(getTypeName, descriptorString, internalName));
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

    public static Type[] array(TypeDescriptor... values) {
        final Type[] types = new Type[values.length];
        for (int i = 0; i < values.length; i++) types[i] = Type.fromDescriptor(values[i]);
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

    private static Type of(String descriptorString) {
        final String descriptor = descriptorString.substring(descriptorString.lastIndexOf(')') + 1);
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
            default ->
                getCached(descriptorCache, descriptor, _ -> new Type(getTypeName(descriptor), descriptor,
                                                                     getInternalName(descriptor)));
        };
    }

    /**
     * The actual size of a method's parameters when represented on the stack.
     * This does not include the caller object, so for non-static method calls this will be +1
     *
     * @param descriptor A method descriptor
     * @return The size taken up (e.g. argument count + wide type offset)
     */
    /* this could be done faster since we don't need to actually construct the types,
     * but it would require a lot of duplicated code.
     */
    public static int parameterSize(@NotNull TypeDescriptor descriptor) {
        int count = 0;
        final String string = descriptor.descriptorString();
        if (string.isEmpty()) return 0; // probably a mess
        if (string.charAt(0) != '(') return 0; // probably a field descriptor
        for (Type type : parameters(descriptor)) count += type.width();
        return count;
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
            return getCached(classCache, thing, _ -> new Type(value.getTypeName(), thing.descriptorString(),
                                                              Type.internalName(value)));
        } else return new Type(value.getTypeName(), Type.descriptorString(value), Type.internalName(value));
    }

    private static <Key, Result>
    Result getCached(Map<Key, SoftReference<Result>> cache, Key key, Function<Key, Result> ifAbsent) {
        final Reference<Result> reference = cache.computeIfAbsent(key, k -> new SoftReference<>(ifAbsent.apply(k)));
        Result result = reference.get();
        if (result != null) return result;
        result = ifAbsent.apply(key);
        cache.put(key, new SoftReference<>(result));
        return result;
    }

    private static Type of(Class<?> value) {
        final Type type = new Type(value.getTypeName(), value.descriptorString(), Type.internalName(value));
        classCache.put(value, new SoftReference<>(type));
        return type;
    }

    public static String internalName(java.lang.reflect.Type klass) {
        if (klass instanceof Class<?> thing) return thing.getName().replace('.', '/');
        if (klass instanceof Type type) return type.internalName;
        if (isPrimitive(klass)) return ofPrimitive(klass).internalName();
        return klass.getTypeName().replaceAll("<.+>", "").replace('.', '/');
    }

    public static final Type BYTE = Type.of(byte.class), SHORT = Type.of(short.class), INT = Type.of(int.class),
        LONG = Type.of(long.class), FLOAT = Type.of(float.class), DOUBLE = Type.of(double.class), BOOLEAN =
        Type.of(boolean.class), CHAR = Type.of(char.class), VOID = Type.of(void.class), OBJECT =
        Type.of(Object.class), STRING = Type.of(String.class), VOID_WRAPPER = Type.of(Void.class);

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
                    if (descriptorString.charAt(0) == '[') yield Class.forName(descriptorString.replace('/', '.'));
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

    public static Type fromInternalName(String internalName) {
        return switch (internalName) {
            case "V" -> Type.VOID;
            case "Z" -> Type.BOOLEAN;
            case "C" -> Type.CHAR;
            case "B" -> Type.BYTE;
            case "S" -> Type.SHORT;
            case "I" -> Type.INT;
            case "L" -> Type.LONG;
            case "F" -> Type.FLOAT;
            case "D" -> Type.DOUBLE;
            default -> internalName.charAt(0) == '[' ? Type.fromDescriptor(() -> internalName)
                : Type.fromDescriptor(() -> "L" + internalName + ";");
        };
    }

    public Type {
        descriptorCache.computeIfAbsent(descriptorString, _ -> new SoftReference<>(this));
    }

    public Class<?> toClass() {
        return toClass(descriptorString, getTypeName);
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

    @Override
    public Optional<? extends ConstantDesc> describeConstable() {
        return Canonical.super.describeConstable();
    }

    @Override
    public boolean validate() {
        return Canonical.super.validate() && RecordConstant.super.validate();
    }

    public Type arrayType() {
        return new Type(getTypeName + "[]", '[' + descriptorString, '[' + descriptorString);
    }

    public Type componentType() {
        if (!descriptorString.startsWith("[") || !getTypeName.endsWith("[]"))
            throw new UnsupportedOperationException("Cannot get component type of non-array " + descriptorString + " "
                                                        + getTypeName);
        final String sub = descriptorString.substring(1);
        final String typeName = getTypeName.substring(0, getTypeName.length() - 2);
        if (sub.charAt(0) == 'L') return new Type(typeName, sub, sub.substring(0, sub.length() - 1));
        return new Type(typeName, sub, sub);
    }

    @Override
    public int width() {
        if (this.equals(LONG) || this.equals(DOUBLE)) return 2;
        if (this.equals(VOID)) return 0; // for the purposes of calculating method width
        return 1;
    }

    @Override
    public boolean isPrimitive() {
        return isPrimitive(descriptorString);
    }

    @Override
    public boolean isInitialisedType() {
        return true;
    }

    @Override
    public boolean isRealType() {
        return true;
    }

    @Override
    public boolean isTypeKnown() {
        return true;
    }

    /**
     * Returns the number of stacked array dimensions this type would have when created.
     * E.g. int -> 0, int[] -> 1, int[][][] -> 3.
     * Effectively, it counts the number of [] brackets after the type name.
     *
     * @return How many array dimensions this type has
     */
    public int arrayDepth() {
        for (int i = 0; i < Short.MAX_VALUE; i++) if (descriptorString.charAt(i) != '[') return i;
        throw new IllegalStateException("Type " + this + " is too big?");
    }

    @Override
    public Type intern() {
        return getCached(descriptorCache, descriptorString, _ -> this);
    }

}
