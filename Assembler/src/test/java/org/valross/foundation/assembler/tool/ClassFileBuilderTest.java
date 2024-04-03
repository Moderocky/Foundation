package org.valross.foundation.assembler.tool;

import org.valross.foundation.Loader;
import org.valross.foundation.assembler.ClassFile;
import org.valross.foundation.detail.Type;
import org.junit.Test;

import static org.valross.foundation.assembler.tool.Access.*;
import static org.valross.foundation.detail.Version.JAVA_21;
import static org.valross.foundation.detail.Version.RELEASE;

public class ClassFileBuilderTest extends ModifiableBuilderTest {

    @Test
    public void simple() {
        final Loader loader = Loader.createDefault();
        final Type type = Type.of("org.example", "Test");
        final ClassFileBuilder builder = new ClassFileBuilder(JAVA_21, type);
        builder.setModifiers(PUBLIC, FINAL, VOLATILE);
        builder.setType(type);
        final ClassFile file = builder.build();
        final Class<?> done = this.load(loader, file, type);
        assert done != null;
        assert Type.of(done).equals(type);
        assert done.getDeclaredFields().length == 0;
    }

    @Override
    protected ModifiableBuilder example() {
        return new ClassFileBuilder(JAVA_21, RELEASE);
    }

}