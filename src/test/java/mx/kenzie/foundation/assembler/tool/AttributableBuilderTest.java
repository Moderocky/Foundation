package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.Loader;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.assembler.ClassFile;
import mx.kenzie.foundation.assembler.attribute.AttributeInfo;
import mx.kenzie.foundation.assembler.attribute.Deprecated;
import mx.kenzie.foundation.assembler.attribute.Synthetic;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public abstract class AttributableBuilderTest {

    protected abstract AttributableBuilder example();

    @Test
    public void synthetic() {
        AttributableBuilder builder = this.example().synthetic();
        assert builder.attributes.size() == 1;
        assert builder.attributes.getFirst() instanceof Synthetic synthetic
            && synthetic.attribute_name_index().ensure().is("Synthetic");
    }

    @Test
    public void deprecated() {
        AttributableBuilder builder = this.example().deprecated();
        assert builder.attributes.size() == 1;
        assert builder.attributes.getFirst() instanceof Deprecated deprecated
            && deprecated.attribute_name_index().ensure().is("Deprecated");
    }

    @Test
    public void attribute() {
    }

    @Test
    public void testAttribute() {
    }

    @Test
    public void attributesCount() {
        AttributableBuilder builder = this.example().deprecated();
        assert builder.attributes.size() == 1;
        builder.synthetic();
        assert builder.attributes.size() == 2;
    }

    @Test
    public void attributes() {
        AttributableBuilder builder = this.example().deprecated();
        assert builder.attributes.size() == 1;
        builder.synthetic();
        assert builder.attributes.size() == 2;
        final AttributeInfo[] attributes = builder.attributes();
        assert attributes != null;
        assert attributes.length == 2;
        assert attributes[0] instanceof Deprecated;
        assert attributes[1] instanceof Synthetic;
    }

    @Test
    public void testAttributes() {
    }

    @Test
    public void helper() {
        assert this.example().helper() != null;
    }

    protected void debug(ClassFile file) throws Throwable {
        final File debug = new File("target/blob.class");
        debug.createNewFile();
        try (OutputStream stream = new FileOutputStream(debug)) {
            file.write(stream);
        }
    }

    protected Class<?> load(Loader loader, ClassFile file, Type type) {
        return loader.loadClass(type.getTypeName(), file.binary());
    }

}