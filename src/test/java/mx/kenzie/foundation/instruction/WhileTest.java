package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import static mx.kenzie.foundation.detail.Modifier.PUBLIC;
import static mx.kenzie.foundation.detail.Modifier.STATIC;
import static mx.kenzie.foundation.Type.BOOLEAN;
import static mx.kenzie.foundation.instruction.Instruction.*;
import static mx.kenzie.foundation.instruction.Instruction.Math.PLUS;
import static mx.kenzie.foundation.instruction.Instruction.Operator.*;

public class WhileTest extends FoundationTest {

    @Test
    public void testCheck() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testCheck");
        method.line(STORE_VAR.intValue(0, ZERO));
        final Block check;
        method.line(check = WHILE.check(COMPARE.ints(LOAD_VAR.intValue(0), LESS, CONSTANT.of(10))));
        check.line(STORE_VAR.intValue(0, SUM.ints(LOAD_VAR.intValue(0), PLUS, ONE)));
        method.line(RETURN.intValue(COMPARE.ints(LOAD_VAR.intValue(0), EQ, CONSTANT.of(10))));
        this.thing.add(method);
    }

    @Test
    public void testDoWhile() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testDoWhile");
        method.line(STORE_VAR.intValue(0, ZERO));
        final Block check;
        method.line(check = WHILE.doWhile(COMPARE.ints(LOAD_VAR.intValue(0), GREATER, CONSTANT.of(10))));
        check.line(STORE_VAR.intValue(0, SUM.ints(LOAD_VAR.intValue(0), PLUS, ONE)));
        method.line(RETURN.intValue(COMPARE.ints(LOAD_VAR.intValue(0), EQ, ONE)));
        this.thing.add(method);
    }

}
