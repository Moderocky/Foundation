package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.Loader;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static mx.kenzie.foundation.detail.Modifier.PUBLIC;
import static mx.kenzie.foundation.detail.Modifier.STATIC;
import static mx.kenzie.foundation.Type.VOID;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class ThrowErrorTest extends FoundationTest {

    @Test(expected = InvocationTargetException.class)
    public void testError() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final PreClass thing = new PreClass("err", "ThrowError");
        final PreMethod method = new PreMethod(PUBLIC, STATIC, VOID, "testError");
        method.line(THROW.error(NEW.of(Error.class).make()));
        thing.add(method);
        final Class<?> loaded = thing.load(Loader.DEFAULT);
        loaded.getDeclaredMethod("testError").invoke(null);
    }

    @Test(expected = InvocationTargetException.class)
    public void testErrorMessage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final PreClass thing = new PreClass("err", "ThrowErrorMessage");
        final PreMethod method = new PreMethod(PUBLIC, STATIC, VOID, "testError");
        method.line(THROW.error(NEW.of(Error.class, String.class).make(CONSTANT.of("hello"))));
        thing.add(method);
        final Class<?> loaded = thing.load(Loader.DEFAULT);
        loaded.getDeclaredMethod("testError").invoke(null);
    }

}
