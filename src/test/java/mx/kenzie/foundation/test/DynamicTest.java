package mx.kenzie.foundation.test;

import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.CodeWriter;
import mx.kenzie.foundation.WriteInstruction;
import org.junit.Test;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PUBLIC;
import static mx.kenzie.foundation.WriteInstruction.*;
import static mx.kenzie.foundation.WriteInstruction.returnEmpty;
import static org.objectweb.asm.Opcodes.*;

public class DynamicTest {
    
    @Test
    public void dynamic() throws Throwable {
        final Method bootstrap = DynamicTest.class.getMethod("bootstrap", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class);
        final Method bootstrap2 = DynamicTest.class.getMethod("bootstrapDyn", MethodHandles.Lookup.class, String.class, MethodType.class, Class.class);
        final Method test1 = DynamicTest.class.getDeclaredMethod("test");
        final Method test2 = DynamicTest.class.getDeclaredMethod("test2", String.class);
        final Method test3 = DynamicTest.class.getDeclaredMethod("test3");
        final Type type = Type.getType(DynamicTest.class);
        final Class<?> cls = new ClassBuilder("org.example.Dynamic")
            .addModifiers(PUBLIC, FINAL)
            .addInterfaces(Runnable.class)
            .addConstructor().finish()
            .addMethod("run").setReturnType(void.class).addModifiers(PUBLIC)
            .writeCode(
                println("Begin dynamic test."),
                invokeDynamic(test1, bootstrap, Type.getType(DynamicTest.class)),
                push("hello!"),
                invokeDynamic(test2, bootstrap, Type.getType(DynamicTest.class)),
                newInstance(new mx.kenzie.foundation.Type(DynamicTest.class), DynamicTest.class.getConstructor()),
                println("Dynamic ins try?"),
                (WriteInstruction) (codeWriter, methodVisitor) -> {
    
                },
                invokeDynamic(new mx.kenzie.foundation.Type(void.class), "test3", new mx.kenzie.foundation.Type[]{new mx.kenzie.foundation.Type(this.getClass())}, getHandle(bootstrap2), Type.getType(DynamicTest.class)),
//                invokeDynamic(test3, bootstrap2, Type.getType(DynamicTest.class)),
                println("Dynamic test works."),
                returnEmpty()
            )
            .finish()
            .compileAndLoad();
    
        assert cls != null;
    
        final Object object = cls.newInstance();
        assert object != null;
        assert object instanceof Runnable;
        ((Runnable) object).run();
    }
    
    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type, Class<?> cls) throws Exception {
        MethodHandle handle = MethodHandles.privateLookupIn(cls, caller).findStatic(cls, name, type);
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapDyn(MethodHandles.Lookup caller, String name, MethodType type, Class<?> cls) throws Exception {
        MethodHandle handle = MethodHandles.lookup().findVirtual(cls, name, MethodType.methodType(Void.TYPE));
        return new ConstantCallSite(handle);
    }
    
    public static CallSite bootstrapT(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle mine = MethodHandles.lookup().findVirtual(DynamicTest.class, "test3", MethodType.methodType(Void.TYPE));
        return new ConstantCallSite(mine);
    }
    
    private void test3() {
        System.out.println("Dynamic instance call.");
    }
    
    private static void test() {
        System.out.println("Dynamic call.");
    }
    
    private static void test2(String value) {
        System.out.println("Dynamic: " + value);
    }
    
    static Handle getHandle(final Method method) {
        final int code;
        if (Modifier.isStatic(method.getModifiers())) code = H_INVOKESTATIC;
        else if (Modifier.isAbstract(method.getModifiers())) code = H_INVOKEINTERFACE;
        else if (Modifier.isPrivate(method.getModifiers())) code = H_INVOKESPECIAL;
        else code = H_INVOKEVIRTUAL;
        return new Handle(code, new mx.kenzie.foundation.Type(method.getDeclaringClass()).internalName(), method.getName(), getDescriptor(new mx.kenzie.foundation.Type(method.getReturnType()), mx.kenzie.foundation.Type.of(method.getParameterTypes())), code == H_INVOKEINTERFACE);
    }
    
    static String getDescriptor(final mx.kenzie.foundation.Type ret, final mx.kenzie.foundation.Type... params) {
        final StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (mx.kenzie.foundation.Type type : params) {
            builder.append(type.descriptorString());
        }
        builder
            .append(")")
            .append(ret.descriptorString());
        return builder.toString();
    }
    
}
