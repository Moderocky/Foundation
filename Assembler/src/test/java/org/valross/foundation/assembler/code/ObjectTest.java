package org.valross.foundation.assembler.code;

import org.junit.Test;
import org.valross.foundation.assembler.tool.*;
import org.valross.foundation.detail.Type;

import java.lang.reflect.InvocationTargetException;

import static org.valross.foundation.assembler.code.OpCode.IFEQ;
import static org.valross.foundation.assembler.code.OpCode.LDC;
import static org.valross.foundation.assembler.tool.Access.PUBLIC;
import static org.valross.foundation.detail.Version.JAVA_22;
import static org.valross.foundation.detail.Version.RELEASE;

public class ObjectTest extends MethodBuilderTest {

    @Test
    public void testConstructorFieldSet()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        {
            ClassFileBuilder builder = new ClassFileBuilder(JAVA_22, RELEASE)
                .setType(Type.of("org.example", "Test"));
            FieldBuilder field = builder.field();
            field.named("foo").ofType(String.class).addModifiers(PUBLIC);
            MethodBuilder constructor = builder.constructor();
            constructor.addModifiers(Access.PUBLIC);
            constructor.parameters(String.class);
            CodeBuilder code = constructor.code();
            code.write(OpCode.ALOAD_0);
            code.write(OpCode.INVOKESPECIAL.constructor(Object.class));
            {
                code.write(OpCode.ALOAD.var(1));
                Branch end = new Branch(), overwrite = new Branch();
                code.write(OpCode.IFNULL.jump(overwrite));
                code.write(OpCode.ALOAD.var(1));
                code.write(OpCode.INVOKEINTERFACE.interfaceMethod(String.class, boolean.class, "isEmpty"));
                code.write(IFEQ.jump(end));
                code.write(overwrite);
                code.write(LDC.value("test"));
                code.write(OpCode.CHECKCAST.type(String.class));
                code.write(OpCode.ASTORE.var(1));
                code.write(end);
                code.write(OpCode.ALOAD_0);
                code.write(OpCode.ALOAD.var(1));
                code.write(OpCode.PUTFIELD.field(builder.asType(), "foo", String.class));
            }
            code.write(OpCode.RETURN);
            Class<?> aClass = this.compileForTest(builder);
            assert aClass != null;
        }
    }

}
