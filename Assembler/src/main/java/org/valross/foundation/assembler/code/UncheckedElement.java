package org.valross.foundation.assembler.code;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;

public record UncheckedElement(byte code, UVec data) implements CodeElement, RecordConstant, UVec {

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        stream.write(code);
        this.data.write(stream);
    }

    @Override
    public int length() {
        return 1 + data.length();
    }

}
