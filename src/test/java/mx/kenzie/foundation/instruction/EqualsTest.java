package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import static mx.kenzie.foundation.Modifier.PUBLIC;
import static mx.kenzie.foundation.Modifier.STATIC;
import static mx.kenzie.foundation.Type.BOOLEAN;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class EqualsTest extends FoundationTest {
    
    @Test
    public void testObjects() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testObjects");
        method.line(RETURN.intValue(EQUALS.objects(CONSTANT.of("hello"), CONSTANT.of("hello"))));
        this.thing.add(method);
        this.tests.add("testObjects");
    }
    
    @Test
    public void testInts() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testInts");
        method.line(RETURN.intValue(EQUALS.ints(CONSTANT.of(6), CONSTANT.of(6))));
        this.thing.add(method);
        this.tests.add("testInts");
    }
    
    @Test
    public void testLongs() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongs");
        method.line(RETURN.intValue(EQUALS.longs(CONSTANT.of(6L), CONSTANT.of(6L))));
        this.thing.add(method);
        this.tests.add("testLongs");
    }
    
    @Test
    public void testFloats() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testFloats");
        method.line(RETURN.intValue(EQUALS.floats(CONSTANT.of(6F), CONSTANT.of(6F))));
        this.thing.add(method);
        this.tests.add("testFloats");
    }
    
    @Test
    public void testDoubles() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testDoubles");
        method.line(RETURN.intValue(EQUALS.doubles(CONSTANT.of(6D), CONSTANT.of(6D))));
        this.thing.add(method);
        this.tests.add("testDoubles");
    }
    
}
