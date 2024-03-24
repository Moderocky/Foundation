package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.Loader;
import mx.kenzie.foundation.PreClass;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import java.lang.reflect.Method;

import static mx.kenzie.foundation.detail.Modifier.PUBLIC;
import static mx.kenzie.foundation.detail.Modifier.STATIC;
import static mx.kenzie.foundation.detail.Type.BOOLEAN;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class EqualsTest extends FoundationTest {

    @Test
    public void testObjects() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testObjects");
        method.line(RETURN.intValue(EQUALS.objects(CONSTANT.of("hello"), CONSTANT.of("hello"))));
        this.thing.add(method);
    }

    @Test
    public void testObjectsFailure() throws Throwable {
        final PreClass failure = new PreClass("org.example.failure", "EqualsTest");
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testObjects");
        method.line(RETURN.intValue(EQUALS.objects(CONSTANT.of("hello"), CONSTANT.of("there"))));
        failure.add(method);
        final Class<?> loaded = failure.load(Loader.DEFAULT);
        final Method target = loaded.getDeclaredMethod("testObjects");
        assert !((boolean) target.invoke(null));
    }

    @Test
    public void testInts() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testInts");
        method.line(RETURN.intValue(EQUALS.ints(CONSTANT.of(6), CONSTANT.of(6))));
        this.thing.add(method);
    }

    @Test
    public void testLongs() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongs");
        method.line(RETURN.intValue(EQUALS.longs(CONSTANT.of(6L), CONSTANT.of(6L))));
        this.thing.add(method);
    }

    @Test
    public void testFloats() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testFloats");
        method.line(RETURN.intValue(EQUALS.floats(CONSTANT.of(6F), CONSTANT.of(6F))));
        this.thing.add(method);
    }

    @Test
    public void testDoubles() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testDoubles");
        method.line(RETURN.intValue(EQUALS.doubles(CONSTANT.of(6D), CONSTANT.of(6D))));
        this.thing.add(method);
    }

}
