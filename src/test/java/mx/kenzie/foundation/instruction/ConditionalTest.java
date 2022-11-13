package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import static mx.kenzie.foundation.Modifier.PUBLIC;
import static mx.kenzie.foundation.Modifier.STATIC;
import static mx.kenzie.foundation.Type.BOOLEAN;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class ConditionalTest extends FoundationTest {
    
    @Test
    public void testCheck() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testCheck");
        method.line(STORE_VAR.booleanValue(0, FALSE));
        final Block check;
        method.line(check = IF.check(TRUE));
        check.line(STORE_VAR.booleanValue(0, TRUE));
        method.line(RETURN.intValue(LOAD_VAR.booleanValue(0)));
        this.thing.add(method);
    }
}
