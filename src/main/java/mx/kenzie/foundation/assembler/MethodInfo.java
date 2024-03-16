package mx.kenzie.foundation.assembler;

import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

public record MethodInfo()
    implements Data, RecordConstant {

    @Override
    public int length() {
        return 0;
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {

    }

}
