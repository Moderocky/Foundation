package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.UVec;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

public interface CodeElement extends Data, UVec {

    static CodeElement fixed(byte... bytes) {
        return new Fixed(bytes);
    }

}

record Fixed(byte[] binary) implements CodeElement, RecordConstant {

    @Override
    public int length() {
        return binary.length;
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        stream.write(binary);
    }

}