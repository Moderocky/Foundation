package org.valross.foundation.assembler.constant;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.vector.U1;
import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;
import java.util.function.BiFunction;

public record ConstantType<Info extends ConstantPoolInfo, Value extends Constable>(U1 value, Class<Info> infoType,
                                                                                   Class<Value> valueType,
                                                                                   BiFunction<ClassFileBuilder.Storage, Value, Info> creator)
    implements UVec, RecordConstant {

    public ConstantType(int value, Class<Info> infoType, Class<Value> valueType, BiFunction<ClassFileBuilder.Storage,
        Value, Info> creator) {
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

    @SuppressWarnings("unchecked")
    public Value unpack(ConstantPoolInfo info) {
        if (info == null) return null;
        if (!infoType.isInstance(info)) throw new IllegalArgumentException("Wrong constant type " + info);
        return (Value) info.unpack();
    }

}
