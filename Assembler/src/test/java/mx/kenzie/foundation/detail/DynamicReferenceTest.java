package mx.kenzie.foundation.detail;

import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.tool.MethodBuilder;
import mx.kenzie.foundation.assembler.tool.MethodBuilderTest;
import org.jetbrains.annotations.Contract;
import org.junit.Test;
import org.valross.constantine.RecordConstant;

import static mx.kenzie.foundation.assembler.code.OpCode.ARETURN;
import static mx.kenzie.foundation.assembler.code.OpCode.LDC;
import static mx.kenzie.foundation.assembler.tool.Access.PUBLIC;
import static mx.kenzie.foundation.assembler.tool.Access.STATIC;
import static mx.kenzie.foundation.detail.Version.JAVA_22;
import static mx.kenzie.foundation.detail.Version.RELEASE;

public class DynamicReferenceTest extends MethodBuilderTest {

    @Contract(pure = true)
    protected MethodBuilder method() {
        return new ClassFileBuilder(JAVA_22, RELEASE).setType(Type.of("org.example", "Test")).method()
                                                     .setModifiers(PUBLIC, STATIC).named("test");
    }

    @Test
    public void of() {
        final Blob blob = new Blob("hello", 5);
        this.check(this.method().returns(String.class).code()
                       .write(LDC.value("hello"), ARETURN)
                       .exit(), "hello");
        this.check(this.method().returns(Blob.class).code()
                       .write(LDC.value(blob), ARETURN)
                       .exit(), blob);
    }

    public record Blob(String word, int number) implements RecordConstant {}

}