package org.valross.foundation.assembler.attribute;

import org.junit.Test;
import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.ClassFile;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.MethodBuilder;
import org.valross.foundation.detail.Type;

import static org.junit.Assert.assertEquals;
import static org.valross.foundation.assembler.code.OpCode.LDC;
import static org.valross.foundation.assembler.tool.Access.PUBLIC;
import static org.valross.foundation.assembler.tool.Access.STATIC;
import static org.valross.foundation.detail.Version.JAVA_22;
import static org.valross.foundation.detail.Version.RELEASE;

public class ConstantValueTest {

    @Test
    public void checkEqualitySame() {

        ClassFileBuilder builder = new ClassFileBuilder(JAVA_22, RELEASE).setType(Type.of("test", "Main"));

        MyValue value = new MyValue(new MyValue2("a", 1), new MyValue2("b", 2));

        MethodBuilder main = builder.addModifiers(PUBLIC, STATIC).method(void.class, "main", String[].class);
        CodeBuilder code = main.code();
        code.write(LDC.value(value));

        ClassFile build = builder.build();

        int initial = build.constant_pool().length;
        code.write(LDC.value(value));
        build = builder.build();

        assertEquals(initial, build.constant_pool().length);
    }

    @Test
    public void checkEqualitySimilar() {

        ClassFileBuilder builder = new ClassFileBuilder(JAVA_22, RELEASE).setType(Type.of("test", "Main"));

        MyValue value = new MyValue(new MyValue2("a", 1), new MyValue2("b", 2));
        MyValue value2 = new MyValue(new MyValue2("a", 1), new MyValue2("b", 2));

        MethodBuilder main = builder.addModifiers(PUBLIC, STATIC).method(void.class, "main", String[].class);
        CodeBuilder code = main.code();
        code.write(LDC.value(value));

        ClassFile build = builder.build();

        int initial = build.constant_pool().length;
        code.write(LDC.value(value2));
        build = builder.build();

        assertEquals(initial, build.constant_pool().length);
    }

    public record MyValue(MyValue2 a, MyValue2 b) implements RecordConstant {

    }

    public record MyValue2(String foo, int bar) implements RecordConstant {

    }

}