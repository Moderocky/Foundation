package mx.kenzie.foundation.test;

import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.opcodes.JavaVersion;
import org.junit.Test;

import java.io.PrintStream;

import static java.lang.reflect.Modifier.*;
import static mx.kenzie.foundation.WriteInstruction.*;

public class SimpleClassBuilderTest {
    
    @Test
    @SuppressWarnings("all")
    public void simple() throws Throwable {
        final Class<?> cls = new ClassBuilder("org.example.Simple")
            .addModifiers(PUBLIC, FINAL)
            .addInterfaces(Runnable.class)
            .addField("box")
                .setType(int.class)
                .addModifiers(PUBLIC)
                .finish()
            .addConstructor()
                .writeCode(
                    loadThis(),
                    invokeSpecial(Object.class.getConstructor()),
                    returnEmpty()
                )
                .finish()
            .addMethod("run")
                .setReturnType(void.class)
                .addModifiers(PUBLIC)
                .writeCode(
                    push("Hello there !"),
                    getField(System.class.getField("out")),
                    duplicate(),
                    storeObject(1),
                    swap(),
                    invoke(PrintStream.class.getMethod("println", String.class)),
                    jump("test"),
                    loadObject(1),
                    push("this never runs"),
                    invoke(PrintStream.class.getMethod("println", String.class)),
                    label("test"),
                    loadObject(1),
                    push(10),
                    invoke(PrintStream.class.getMethod("println", int.class)),
                    push(true),
                    jumpIfFalse("first"),
                    jump("second"),
                    label("first"),
                    loadObject(1),
                    push("this never prints"),
                    invoke(PrintStream.class.getMethod("println", String.class)),
                    label("second"),
                    push(true),
                    jumpIfTrue("third"),
                    loadObject(1),
                    push("this never prints"),
                    invoke(PrintStream.class.getMethod("println", String.class)),
                    label("third"),
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
    
    @Test
    public void subroutines() throws Throwable {
        final Class<?> cls = new ClassBuilder(new Type("org.example.Subroutines"), JavaVersion.JAVA_5)
            .addModifiers(PUBLIC, FINAL)
            .setComputation(1)
            .addInterfaces(Runnable.class)
            .addConstructor()
            .finish()
            .addMethod("run")
            .setReturnType(void.class)
            .addModifiers(PUBLIC)
            .writeCode(
                jumpSubroutine("blob"),
                println("before subroutine"),
                jump("end"),
                label("blob"),
                storeObject(2),
                println("inside subroutine"),
                exitSubroutine(2),
                label("end"),
                println("after subroutine"),
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
    
    @Test
    public void special() throws Throwable {
        final Class<?> cls = new ClassBuilder("org.example.Special")
            .addModifiers(PUBLIC, FINAL)
            .addInterfaces(Runnable.class)
            .addConstructor().finish()
            .addMethod("run").setReturnType(void.class).addModifiers(PUBLIC)
            .writeCode(
                println("Printing works."),
                push(true),
                assertTrue(),
                push(1),
                printTopSmall(),
                newInstance(new Type(RuntimeException.class), RuntimeException.class.getConstructor()),
                printTopObject(),
                push("Print top works."),
                printTopObject(),
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
    
    @Test
    public void numbers() throws Throwable {
        final Class<?> cls = new ClassBuilder("org.example.Numbers")
            .addModifiers(PUBLIC, FINAL)
            .addInterfaces(Runnable.class)
            .addConstructor().finish()
            .addMethod("run").setReturnType(void.class).addModifiers(PUBLIC)
            .writeCode(
                push(10),
                storeSmall(1),
                incrementSmall(1, 1),
                loadSmall(1),
                push(11),
                jumpIfEQ("first"),
                throwErrorMessage("Failure in increment test."),
                label("first"),
                allocate(new Type(Long.class)),
                duplicate(),
                loadConstant(1000L),
                invokeSpecial(Long.class.getConstructor(long.class)),
                invoke(Long.class.getMethod("longValue")),
                loadConstant(1000L),
                subtractLong(),
                convert(long.class, int.class),
                jumpIf0("second"),
                throwErrorMessage("Failure in constant conversion test."),
                label("second"),
                println("All number tests passed fine."),
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
    
    @Test
    public void arrays() throws Throwable {
        final Class<?> cls = new ClassBuilder("org.example.Arrays")
            .addModifiers(PUBLIC, FINAL)
            .addInterfaces(Runnable.class)
            .addConstructor().finish()
            .addMethod("run").setReturnType(void.class).addModifiers(PUBLIC)
            .writeCode(
                push(2),
                newArray(String.class),
                duplicate(),
                storeObject(1),
                push("hello"),
                arrayStoreObject(0),
                loadObject(1),
                push("there"),
                arrayStoreObject(1),
                loadObject(1),
                arrayLoadObject(0),
                push("hello"),
                jumpIfEquals("a"),
                throwErrorMessage("Retrieval failed."),
                label("a"),
                loadObject(1),
                arrayLoadObject(1),
                push("there"),
                jumpIfEquals("b"),
                throwErrorMessage("Retrieval failed."),
                label("b"),
                loadObject(1),
                arrayLength(),
                push(1),
                subtractSmall(),
                assertTrue(),
                newArray(int.class, 6),
                duplicate(),
                storeObject(2),
                arrayLength(),
                push(5),
                subtractSmall(),
                assertTrue(),
                loadObject(2),
                duplicate(),
                push(66),
                arrayStore(int.class, 0),
                arrayLoad(int.class, 0),
                push(65),
                subtractSmall(),
                assertTrue(),
                println("All array tests passed fine."),
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
    
    @Test
    public void exceptions() throws Throwable {
        final Class<?> cls = new ClassBuilder("org.example.Exceptions")
            .addModifiers(PUBLIC, FINAL)
            .addInterfaces(Runnable.class)
            .addConstructor().finish()
            .addMethod("run").setReturnType(void.class).addModifiers(PUBLIC)
            .writeCode(
                tryCatch(new Type(AssertionError.class), "try1", "try2", "catch"),
                label("try1"),
                push(0),
                assertTrue(),
                label("try2"),
                throwErrorMessage("Catch jump failed."),
                jump("exit"),
                label("catch"),
                label("exit"),
                trySection(
                    push(1),
                    assertTrue()
                ).finallySection(
                    println("Finally section ran correctly.")
                ),
                trySection(
                    push(0),
                    assertTrue(),
                    throwErrorMessage("Finally jump failed.")
                ).finallySection(
                    println("All exception tests passed fine.")
                ),
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
    
}
