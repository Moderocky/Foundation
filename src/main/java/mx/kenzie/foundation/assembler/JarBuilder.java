package mx.kenzie.foundation.assembler;

import mx.kenzie.foundation.SourceReader;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.PostCompileClass;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarBuilder implements AutoCloseable {
    
    protected final File file;
    protected final ZipOutputStream stream;
    
    public JarBuilder(File file) throws FileNotFoundException {
        this.file = file;
        this.stream = new ZipOutputStream(new FileOutputStream(file));
    }
    
    public JarBuilder write(Class<?> type) throws IOException {
        final byte[] source = SourceReader.getSource(type);
        final ZipEntry entry = new ZipEntry(new Type(type).internalName() + ".class");
        this.stream.putNextEntry(entry);
        this.stream.write(source, 0, source.length);
        this.stream.closeEntry();
        return this;
    }
    
    public JarBuilder write(String path, InputStream data) throws IOException {
        final ZipEntry entry = new ZipEntry(path);
        this.stream.putNextEntry(entry);
        data.transferTo(this.stream);
        this.stream.closeEntry();
        return this;
    }
    
    public JarBuilder write(PostCompileClass... classes) throws IOException {
        for (final PostCompileClass result : classes) {
            this.write(result.internalName() + ".class", result.code());
        }
        return this;
    }
    
    public JarBuilder write(String path, byte[] data) throws IOException {
        final ZipEntry entry = new ZipEntry(path);
        this.stream.putNextEntry(entry);
        this.stream.write(data, 0, data.length);
        this.stream.closeEntry();
        return this;
    }
    
    public JarBuilder manifest(Manifest manifest) throws IOException {
        final byte[] data = manifest.data();
        final ZipEntry entry = new ZipEntry("META-INF/MANIFEST.MF");
        this.stream.putNextEntry(entry);
        this.stream.write(data, 0, data.length);
        this.stream.closeEntry();
        return this;
    }
    
    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
