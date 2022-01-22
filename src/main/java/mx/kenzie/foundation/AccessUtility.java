package mx.kenzie.foundation;

import org.objectweb.asm.Handle;
import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

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
    
    static Handle getHandle(final Constructor<?> constructor) {
        return new Handle(H_INVOKESPECIAL, new Type(constructor.getDeclaringClass()).internalName(), "<init>", AccessUtility.getDescriptor(new Type(void.class), Type.of(constructor.getParameterTypes())), false);
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
    
    static Handle getHandle(final Method method) {
        final int code;
        if (Modifier.isStatic(method.getModifiers())) code = H_INVOKESTATIC;
        else if (Modifier.isAbstract(method.getModifiers())) code = H_INVOKEINTERFACE;
        else if (Modifier.isPrivate(method.getModifiers())) code = H_INVOKESPECIAL;
        else code = H_INVOKEVIRTUAL;
        return new Handle(code, new Type(method.getDeclaringClass()).internalName(), method.getName(), AccessUtility.getDescriptor(new Type(method.getReturnType()), Type.of(method.getParameterTypes())), code == H_INVOKEINTERFACE);
    }
    
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
