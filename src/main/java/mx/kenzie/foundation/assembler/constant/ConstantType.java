package mx.kenzie.foundation.assembler.constant;

import mx.kenzie.foundation.assembler.ConstantPoolInfo;
import mx.kenzie.foundation.assembler.U1;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;
import java.util.function.BiFunction;

public record ConstantType<Info extends ConstantPoolInfo, Value extends Constable>(U1 value, Class<Info> infoType,
                                                                                   Class<Value> valueType,
                                                                                   BiFunction<ClassFileBuilder.Helper, Value, Info> creator)
    implements UVec, RecordConstant {

    public ConstantType(int value, Class<Info> infoType, Class<Value> valueType, BiFunction<ClassFileBuilder.Helper, Value, Info> creator) {
        this(U1.valueOf(value), infoType, valueType, creator);
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public byte[] binary() {
        return value.binary();
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.value.write(stream);
    }

    public int indices() {
        if (infoType == LongNumberInfo.class) return 2;
        return 1;
    }

}
