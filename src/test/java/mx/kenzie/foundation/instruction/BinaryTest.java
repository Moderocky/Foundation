package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import static mx.kenzie.foundation.Modifier.PUBLIC;
import static mx.kenzie.foundation.Modifier.STATIC;
import static mx.kenzie.foundation.Type.BOOLEAN;
import static mx.kenzie.foundation.instruction.Instruction.*;
import static mx.kenzie.foundation.instruction.Instruction.Operator.*;

public class BinaryTest extends FoundationTest {

    @Test
    public void testObjects() {
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testObjectsEQ");
            method.line(RETURN.intValue(COMPARE.objects(CONSTANT.of("hello"), EQ, CONSTANT.of("hello"))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testObjectsNOTEQ");
            method.line(RETURN.intValue(COMPARE.objects(CONSTANT.of("hello"), NOT_EQ, CONSTANT.of("there"))));
            this.thing.add(method);
        }
    }

    @Test
    public void testInts() {
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testIntsEQ");
            method.line(RETURN.intValue(COMPARE.ints(ONE, EQ, ONE)));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testIntsNOTEQ");
            method.line(RETURN.intValue(COMPARE.ints(ONE, NOT_EQ, ZERO)));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testIntsLT");
            method.line(RETURN.intValue(COMPARE.ints(ZERO, LESS, ONE)));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testIntsLE");
            method.line(RETURN.intValue(COMPARE.ints(ONE, LESS_EQ, ONE)));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testIntsGT");
            method.line(RETURN.intValue(COMPARE.ints(ONE, GREATER, ZERO)));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testIntsGE");
            method.line(RETURN.intValue(COMPARE.ints(ZERO, GREATER_EQ, ZERO)));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testIntsOR");
            method.line(RETURN.intValue(COMPARE.ints(ONE, OR, ZERO)));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testIntsAND");
            method.line(RETURN.intValue(COMPARE.ints(ONE, AND, ONE)));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testIntsXOR");
            method.line(RETURN.intValue(COMPARE.ints(ONE, XOR, ZERO)));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testIntsXOR2");
            method.line(RETURN.intValue(COMPARE.ints(ONE, XOR, COMPARE.ints(ONE, XOR, ONE))));
            this.thing.add(method);
        }
    }

    @Test
    public void testLongs() {
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongsEQ");
            method.line(RETURN.intValue(COMPARE.longs(CONSTANT.of(1L), EQ, CONSTANT.of(1L))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongsNOTEQ");
            method.line(RETURN.intValue(COMPARE.longs(CONSTANT.of(1L), NOT_EQ, CONSTANT.of(0L))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongsLT");
            method.line(RETURN.intValue(COMPARE.longs(CONSTANT.of(0L), LESS, CONSTANT.of(1L))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongsLE");
            method.line(RETURN.intValue(COMPARE.longs(CONSTANT.of(1L), LESS_EQ, CONSTANT.of(1L))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongsGT");
            method.line(RETURN.intValue(COMPARE.longs(CONSTANT.of(1L), GREATER, CONSTANT.of(0L))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongsGE");
            method.line(RETURN.intValue(COMPARE.longs(CONSTANT.of(0L), GREATER_EQ, CONSTANT.of(0L))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongsOR");
            method.line(RETURN.intValue(COMPARE.longs(CONSTANT.of(1L), OR, CONSTANT.of(0L))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongsAND");
            method.line(RETURN.intValue(COMPARE.longs(CONSTANT.of(1L), AND, CONSTANT.of(1L))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongsXOR");
            method.line(RETURN.intValue(COMPARE.longs(CONSTANT.of(1L), XOR, CONSTANT.of(0L))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLongsXOR2");
            method.line(RETURN.intValue(COMPARE.ints(ONE, XOR, COMPARE.longs(CONSTANT.of(1L), XOR, CONSTANT.of(1L)))));
            this.thing.add(method);
        }
    }

    @Test
    public void testFloats() {
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testFloatsEQ");
            method.line(RETURN.intValue(COMPARE.floats(CONSTANT.of(1F), EQ, CONSTANT.of(1F))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testFloatsNOTEQ");
            method.line(RETURN.intValue(COMPARE.floats(CONSTANT.of(1F), NOT_EQ, CONSTANT.of(0F))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testFloatsLT");
            method.line(RETURN.intValue(COMPARE.floats(CONSTANT.of(0F), LESS, CONSTANT.of(1F))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testFloatsLE");
            method.line(RETURN.intValue(COMPARE.floats(CONSTANT.of(1F), LESS_EQ, CONSTANT.of(1F))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testFloatsGT");
            method.line(RETURN.intValue(COMPARE.floats(CONSTANT.of(1F), GREATER, CONSTANT.of(0F))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testFloatsGE");
            method.line(RETURN.intValue(COMPARE.floats(CONSTANT.of(0F), GREATER_EQ, CONSTANT.of(0F))));
            this.thing.add(method);
        }
    }

    @Test
    public void testDoubles() {
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testDoublesEQ");
            method.line(RETURN.intValue(COMPARE.doubles(CONSTANT.of(1D), EQ, CONSTANT.of(1D))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testDoublesNOTEQ");
            method.line(RETURN.intValue(COMPARE.doubles(CONSTANT.of(1D), NOT_EQ, CONSTANT.of(0D))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testDoublesLT");
            method.line(RETURN.intValue(COMPARE.doubles(CONSTANT.of(0D), LESS, CONSTANT.of(1D))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testDoublesLE");
            method.line(RETURN.intValue(COMPARE.doubles(CONSTANT.of(1D), LESS_EQ, CONSTANT.of(1D))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testDoublesGT");
            method.line(RETURN.intValue(COMPARE.doubles(CONSTANT.of(1D), GREATER, CONSTANT.of(0D))));
            this.thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testDoublesGE");
            method.line(RETURN.intValue(COMPARE.doubles(CONSTANT.of(0D), GREATER_EQ, CONSTANT.of(0D))));
            this.thing.add(method);
        }
    }

}
