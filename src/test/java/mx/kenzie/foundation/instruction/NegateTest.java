package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import static mx.kenzie.foundation.detail.Modifier.PUBLIC;
import static mx.kenzie.foundation.detail.Modifier.STATIC;
import static mx.kenzie.foundation.Type.BOOLEAN;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class NegateTest extends FoundationTest {

    @Test
    public void testInvert() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testInvert");
        method.line(STORE_VAR.booleanValue(0, FALSE));
        method.line(RETURN.intValue(NOT.invert(LOAD_VAR.booleanValue(0))));
        this.thing.add(method);
    }

}
