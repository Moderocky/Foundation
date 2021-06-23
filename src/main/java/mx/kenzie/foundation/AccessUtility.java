package mx.kenzie.foundation;

import sun.misc.Unsafe;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;

class AccessUtility {
    
    static Unsafe UNSAFE;
    private static long offset;
    
    //region Initialise Unsafe
    static {
        try {
            UNSAFE = AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>) () -> {
                final Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                return (Unsafe) field.get(null);
            });
            final Field field = Class.class.getDeclaredField("module");
            offset = UNSAFE.objectFieldOffset(field);
            UNSAFE.putObject(AccessUtility.class, offset, Object.class.getModule());
        } catch (Throwable ex) {
            ex.printStackTrace();
            UNSAFE = null;
        }
    }
    //endregion
    
    //region Name Utilities
    static String getDescriptor(final Type ret, final Type... params) {
        final StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (Type type : params) {
            builder.append(type.descriptorString());
        }
        builder
            .append(")")
            .append(ret.descriptorString());
        return builder.toString();
    }
    //endregion
    
    static Method getMatchingMethod(Object object, Method method) {
        Method match = null;
        for (Method m : object.getClass().getDeclaredMethods()) {
            if (m.getName().equals(method.getName())
                && Arrays.equals(m.getParameterTypes(), method.getParameterTypes())
                && method.getReturnType().isAssignableFrom(m.getReturnType())
            ) return m;
        }
        if (object.getClass().getSuperclass() == Object.class) return match;
        return getMatchingMethod(object.getClass().getSuperclass(), method);
    }
    
    static Method getMatchingMethod(Object object, Class<?> type, String name, Class<?>... params) {
        Method match = null;
        for (Method m : object.getClass().getDeclaredMethods()) {
            if (m.getName().equals(name)
                && Arrays.equals(m.getParameterTypes(), params)
                && type.isAssignableFrom(m.getReturnType())
            ) return m;
        }
        if (object.getClass().getSuperclass() == Object.class) return match;
        return getMatchingMethod(object.getClass().getSuperclass(), type, name, params);
    }
    
    static void access(final AccessibleObject object) {
        object.setAccessible(true);
    }
    
    static void moveModule(Class<?> from, Class<?> to) {
        UNSAFE.putObject(from, offset, to.getModule());
    }
    
}
