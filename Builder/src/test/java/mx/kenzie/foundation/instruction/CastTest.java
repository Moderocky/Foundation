package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import static mx.kenzie.foundation.detail.Modifier.PUBLIC;
import static mx.kenzie.foundation.detail.Modifier.STATIC;
import static mx.kenzie.foundation.detail.Type.BOOLEAN;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class CastTest extends FoundationTest {

    @Test
    public void testNumber() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testNumber");
        method.line(STORE_VAR.intValue(0, PUSH.byteValue(10)));
        method.line(STORE_VAR.floatValue(0, CAST.number(LOAD_VAR.intValue(0), Convert.INT_TO_FLOAT)));
        method.line(STORE_VAR.doubleValue(0, CAST.number(LOAD_VAR.floatValue(0), Convert.FLOAT_TO_DOUBLE)));
        method.line(STORE_VAR.longValue(0, CAST.number(LOAD_VAR.doubleValue(0), Convert.DOUBLE_TO_LONG)));
        method.line(STORE_VAR.intValue(0, CAST.number(LOAD_VAR.longValue(0), Convert.LONG_TO_INT)));
        method.line(RETURN.intValue(EQUALS.ints(LOAD_VAR.intValue(0), PUSH.byteValue(10))));
        this.thing.add(method);
    }

    @Test
    public void testObject() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testObject");
        method.line(STORE_VAR.object(0, CONSTANT.of("hello")));
        method.line(STORE_VAR.object(0, CAST.object(LOAD_VAR.object(0), Object.class)));
        method.line(STORE_VAR.object(0, CAST.object(LOAD_VAR.object(0), CharSequence.class)));
        method.line(RETURN.intValue(EQUALS.objects(LOAD_VAR.object(0), CONSTANT.of("hello"))));
        this.thing.add(method);
    }

}
