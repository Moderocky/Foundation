package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import static mx.kenzie.foundation.detail.Modifier.PUBLIC;
import static mx.kenzie.foundation.detail.Modifier.STATIC;
import static mx.kenzie.foundation.detail.Type.BOOLEAN;
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

    @Test
    public void testElse() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testElse");
        method.line(STORE_VAR.booleanValue(0, FALSE));
        final Block.If check;
        method.line(check = IF.check(FALSE));
        check.line(STORE_VAR.booleanValue(0, FALSE));
        final Block otherwise = check.elseBlock();
        otherwise.line(STORE_VAR.booleanValue(0, TRUE));
        final Block.If next;
        method.line(next = IF.check(LOAD_VAR.intValue(0)));
        next.line(RETURN.intValue(TRUE));
        next.elseBlock().line(RETURN.intValue(FALSE));
        this.thing.add(method);
    }

}
