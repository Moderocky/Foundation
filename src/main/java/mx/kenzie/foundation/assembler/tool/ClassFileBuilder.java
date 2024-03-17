package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.Member;
import mx.kenzie.foundation.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.assembler.*;
import mx.kenzie.foundation.assembler.attribute.AttributeInfo;
import mx.kenzie.foundation.assembler.constant.*;
import org.intellij.lang.annotations.MagicConstant;
import org.valross.constantine.Constantive;

import java.lang.constant.Constable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.*;

public class ClassFileBuilder implements Constantive {

    protected U4 magic;
    protected U2 minor_version, major_version, access_flags = U2.valueOf(0);
    protected List<ConstantPoolInfo> constantPool;
    U2 this_class;
    U2 super_class;
    U2 interfaces_count;
    U2[] interfaces;
    //interfaces_count
    U2 fields_count;
    FieldInfo[] fields; //fields_count
    U2 methods_count;
    MethodInfo[] methods; //methods_count
    U2 attributes_count;
    AttributeInfo[] attributes; //attributes_count

    public ClassFileBuilder(int magic, int majorVersion, int minorVersion) {
        this.magic = U4.valueOf(magic);
        this.major_version = U2.valueOf(majorVersion);
        this.minor_version = U2.valueOf(minorVersion);
        this.constantPool = new LinkedList<>();
    }

    public ClassFileBuilder setAccessFlags(@MagicConstant(valuesFromClass = Access.class) int flags) {
        this.access_flags = U2.valueOf(flags);
        return this;
    }

    public ClassFileBuilder addAccessFlags(@MagicConstant(valuesFromClass = Access.class) int flags) {
        this.access_flags = U2.valueOf(this.access_flags.value() | flags);
        return this;
    }

    protected U2 constantPoolCount() {
        int size = 0;
        for (ConstantPoolInfo info : constantPool) size += info.tag().indices();
        return U2.valueOf(size + 1);
    }

    protected ConstantPoolInfo[] constantPool() {
        this.constantPool.sort(ConstantPoolInfo::compareTo);
        final List<ConstantPoolInfo> list = new ArrayList<>(constantPool.size() + 8); // guesswork
        for (ConstantPoolInfo info : constantPool) {
            list.add(info);
            if (info instanceof LongNumberInfo<?>) list.add(new DeadSpaceInfo());
        }
        return list.toArray(new ConstantPoolInfo[0]);
    }

    public ClassFile build() {
        return new ClassFile(magic, minor_version, major_version, constantPoolCount(), constantPool(), access_flags, this_class, super_class, interfaces_count, interfaces, fields_count, fields, methods_count, methods, attributes_count, attributes);
    }

    @Override
    public ClassFile constant() {
        return this.build();
    }

    public class Helper {

        public <Value extends Constable> PoolReference constant(ConstantType<?, Value> type, Value value) {
            for (ConstantPoolInfo info : constantPool) {
                if (info.tag() != type) continue;
                if (info.is(value)) return new PoolReference(constantPool, info);
            }
            final ConstantPoolInfo info = type.creator().apply(this, value);
            constantPool.add(info);
            return new PoolReference(constantPool, info);
        }

        public Utf8Info valueOf(String value) {
            return Utf8Info.of(value);
        }

        public StringInfo valueOfString(String value) {
            return new StringInfo(this.constant(UTF8, value));
        }

        public ClassInfo valueOf(Type value) {
            return new ClassInfo(this.constant(UTF8, value.internalName()));
        }

        public SignatureInfo valueOf(Signature value) {
            return new SignatureInfo(this.constant(UTF8, value.name()), this.constant(UTF8, value.descriptorString()));
        }

        public ReferenceInfo valueOfField(Member member) {
            return this.valueOf(FIELD_REFERENCE, member);
        }

        public ReferenceInfo valueOfMethod(Member member) {
            return this.valueOf(METHOD_REFERENCE, member);
        }

        public ReferenceInfo valueOfInterfaceMethod(Member member) {
            return this.valueOf(INTERFACE_METHOD_REFERENCE, member);
        }

        public ReferenceInfo valueOf(ConstantType<ReferenceInfo, Member> tag, Member member) {
            return new ReferenceInfo(tag, this.constant(TYPE, member.owner()), this.constant(NAME_AND_TYPE, member.signature()));
        }

        public NumberInfo<Integer> valueOf(Integer i) {
            return new NumberInfo<>(INTEGER, U4.fromSigned(i));
        }

        public NumberInfo<Float> valueOf(Float f) {
            return new NumberInfo<>(FLOAT, U4.fromSigned(f));
        }

        public LongNumberInfo<Long> valueOf(Long j) {
            final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES).putLong(j);
            return new LongNumberInfo<>(LONG, U4.fromSigned(buffer.getInt(0)), U4.fromSigned(buffer.getInt(4)));
        }

        public LongNumberInfo<Double> valueOf(Double d) {
            final ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES).putDouble(d);
            return new LongNumberInfo<>(DOUBLE, U4.fromSigned(buffer.getInt(0)), U4.fromSigned(buffer.getInt(4)));
        }

    }

}
