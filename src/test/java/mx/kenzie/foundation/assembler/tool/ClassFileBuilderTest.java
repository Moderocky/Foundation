package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.assembler.ClassFile;
import org.junit.Test;

import static mx.kenzie.foundation.assembler.tool.Version.JAVA_21;
import static mx.kenzie.foundation.assembler.tool.Version.RELEASE;

public class ClassFileBuilderTest {

    @Test
    public void simple() {
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, RELEASE);
        final ClassFileBuilder.Helper helper = builder.new Helper();
        helper.setType(Type.of("org.example", "Test"));
        final ClassFile file = builder.build();
    }

}