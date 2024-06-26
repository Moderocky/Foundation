package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import static org.valross.foundation.detail.Modifier.PUBLIC;
import static org.valross.foundation.detail.Modifier.STATIC;
import static org.valross.foundation.detail.Type.BOOLEAN;
import static mx.kenzie.foundation.instruction.Instruction.*;
import static mx.kenzie.foundation.instruction.Instruction.Operator.EQ;
import static mx.kenzie.foundation.instruction.Instruction.Operator.LESS;

public class ForEachTest extends FoundationTest {

    @Test
    public void testLoop() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testLoop");
        final Block check = FOR.loop(
            STORE_VAR.intValue(0, ZERO),
            COMPARE.ints(LOAD_VAR.intValue(0), LESS, PUSH.byteValue(10)),
            INCREMENT.var(0, 1)
                                    );
        method.line(check);
        method.line(RETURN.intValue(COMPARE.ints(LOAD_VAR.intValue(0), EQ, PUSH.byteValue(10))));
        this.thing.add(method);
    }

}
