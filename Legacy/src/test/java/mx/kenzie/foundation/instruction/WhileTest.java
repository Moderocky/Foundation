package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.Block;
import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import static org.valross.foundation.detail.Modifier.PUBLIC;
import static org.valross.foundation.detail.Modifier.STATIC;
import static org.valross.foundation.detail.Type.BOOLEAN;
import static mx.kenzie.foundation.instruction.Instruction.*;
import static mx.kenzie.foundation.instruction.Instruction.Math.PLUS;
import static mx.kenzie.foundation.instruction.Instruction.Operator.*;

public class WhileTest extends FoundationTest {

    // Constants for integer values
    private static final int INITIAL_VALUE = 0;
    private static final int LOOP_INCREMENT = 1;
    private static final int LOOP_TERMINATION_CONDITION = 10;
    private static final int EXPECTED_FINAL_VALUE = 10;
    private static final int DO_WHILE_EXPECTED_VALUE = 1;

    /**
     * Tests a while loop that increments a variable until it reaches 10.
     * The method initializes a variable to 0, then uses a while loop to increment it
     * until the value is no longer less than 10. Finally, it returns true if the
     * variable equals 10.
     */
    @Test
    public void testWhileLoopIncrementUntilTen() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testWhileLoopIncrementUntilTen");

        method.line(STORE_VAR.intValue(0, CONSTANT.of(INITIAL_VALUE)));

        final Block whileBlock;
        method.line(whileBlock = WHILE.check(
                COMPARE.ints(LOAD_VAR.intValue(0), LESS, CONSTANT.of(LOOP_TERMINATION_CONDITION))
        ));

        whileBlock.line(
                STORE_VAR.intValue(0, SUM.ints(LOAD_VAR.intValue(0), PLUS, CONSTANT.of(LOOP_INCREMENT)))
        );

        method.line(
                RETURN.intValue(COMPARE.ints(LOAD_VAR.intValue(0), EQ, CONSTANT.of(EXPECTED_FINAL_VALUE))
                ));

        this.thing.add(method);
    }

    /**
     * Tests a do-while loop that increments a variable and checks the condition afterward.
     * The method initializes a variable to 0, then uses a do-while loop to increment it
     * at least once, continuing as long as the variable is greater than 10. Finally,
     * it returns true if the variable equals 1.
     */
    @Test
    public void testDoWhileLoopIncrementAndCheckCondition() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testDoWhileLoopIncrementAndCheckCondition");

        method.line(STORE_VAR.intValue(0, CONSTANT.of(INITIAL_VALUE)));

        final Block doWhileBlock;
        method.line(doWhileBlock = WHILE.doWhile(
                COMPARE.ints(LOAD_VAR.intValue(0), GREATER, CONSTANT.of(LOOP_TERMINATION_CONDITION))
        ));

        doWhileBlock.line(
                STORE_VAR.intValue(0, SUM.ints(LOAD_VAR.intValue(0), PLUS, CONSTANT.of(LOOP_INCREMENT)))
        );

        method.line(
                RETURN.intValue(COMPARE.ints(LOAD_VAR.intValue(0), EQ, CONSTANT.of(DO_WHILE_EXPECTED_VALUE))
                ));

        this.thing.add(method);
    }

}
