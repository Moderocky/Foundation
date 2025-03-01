package org.valross.foundation.assembler.attribute.frame;

import org.valross.constantine.Constant;
import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.constant.ConstantPoolInfo;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.ProgramStack;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.foundation.detail.Type;
import org.valross.foundation.detail.TypeHint;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public interface VerificationTypeInfo extends UVec, Constant {

    VerificationTypeInfo TOP = new TinyTypeInfo(0), INTEGER = new TinyTypeInfo(1), FLOAT = new TinyTypeInfo(2),
        DOUBLE = new TinyTypeInfo(3), LONG = new TinyTypeInfo(4), NULL = new TinyTypeInfo(5), UNINITIALISED_THIS =
        new TinyTypeInfo(6);

    static VerificationTypeInfo[] of(ClassFileBuilder.Storage storage, TypeHint... types) {
        final VerificationTypeInfo[] infos = new VerificationTypeInfo[types.length];
        if (infos.length == 0) return infos;
        for (int i = 0; i < types.length; i++) infos[i] = of(storage, types[i]);
        return infos;
    }

    static VerificationTypeInfo of(ClassFileBuilder.Storage storage, TypeHint type) {
        if (Objects.equals(type, TypeHint.none())) return NULL;
        if (Objects.equals(type, ProgramStack.TOP)) return TOP;
        if (type.isPrimitive()) return switch (type.getTypeName()) {
            case "int", "short", "byte", "boolean", "char" -> INTEGER;
            case "float" -> FLOAT;
            case "double" -> DOUBLE;
            case "long" -> LONG;
            default -> throw new IllegalArgumentException(type.toString());
        };
        if (type instanceof TypeHint.This) return UNINITIALISED_THIS;
        if (type instanceof TypeHint.Uninitialised uninitialised)
            return new UninitializedTypeInfo(uninitialised.offset());
        else if (type.isInitialisedType() && type.isTypeKnown()) return new ObjectTypeInfo(type.asType(), storage);
        else if (type instanceof TypeHint.Guess uninitialised)
            return new ObjectTypeInfo(type.asType(), storage);
        throw new IllegalArgumentException("Don't know how to put " + type + " in the frame.");
    }

    byte tag();

}

record UninitializedTypeInfo(UVec offset) implements VerificationTypeInfo, RecordConstant {

    @Override
    public byte tag() {
        return 8;
    }

    @Override
    public int length() {
        return 3;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write(this.tag());
        this.offset.write(stream);
    }

}

record ObjectTypeInfo(UVec cpool_index) implements VerificationTypeInfo, RecordConstant {

    ObjectTypeInfo(Type type, ClassFileBuilder.Storage storage) {
        this(storage.constant(ConstantPoolInfo.TYPE, type));
    }

    @Override
    public byte tag() {
        return 7;
    }

    @Override
    public int length() {
        return 3;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write(this.tag());
        this.cpool_index.write(stream);
    }

}

record TinyTypeInfo(byte tag) implements VerificationTypeInfo, RecordConstant {

    TinyTypeInfo(int tag) {
        this((byte) tag);
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public byte[] binary() {
        return new byte[] {tag};
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write(tag);
    }

}
