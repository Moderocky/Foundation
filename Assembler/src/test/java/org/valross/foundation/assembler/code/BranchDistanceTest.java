package org.valross.foundation.assembler.code;

import org.junit.Test;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.MethodBuilder;
import org.valross.foundation.assembler.tool.MethodBuilderTest;

import java.lang.reflect.InvocationTargetException;

import static org.valross.foundation.assembler.code.OpCode.*;

public class BranchDistanceTest extends MethodBuilderTest {


    @Test
    public void testAALOAD() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MethodBuilder method = this.method();
        CodeBuilder code = method.code();
        for (int i = 0; i < 24; i++) {
            code.write(ACONST_NULL, CHECKCAST.type(Object.class), ASTORE.var(i + 1));
        }
        Branch start = new Branch(), end = new Branch();
        code.write(start);
        code.write(ALOAD.var(7), IFNULL.jump(end));
        for (int i = 0; i < 6; i++) {
            code.write(ACONST_NULL, CHECKCAST.type(Object.class), ASTORE.var(18 + i));
        }
        code.write(GOTO.jump(start));
        code.write(end);
        code.write(ACONST_NULL, ASTORE_0);
        code.write(ALOAD_0, ARETURN);
        assert this.compileForTest(method.returns(Object.class)).invoke(null) == null;
    }


}
