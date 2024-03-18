package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.Member;
import mx.kenzie.foundation.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.assembler.*;
import mx.kenzie.foundation.assembler.attribute.AttributeInfo;
import mx.kenzie.foundation.assembler.constant.*;
import mx.kenzie.foundation.assembler.error.ClassBuilderException;
import org.valross.constantine.Constantive;

import java.lang.constant.Constable;
import java.lang.invoke.TypeDescriptor;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.*;

public class ClassFileBuilder implements Constantive {

    private static final U4 MAGIC = U4.valueOf(0xCAFEBABE);
    protected final U4 magic;
    protected final U2 minor_version, major_version;
    protected U2 access_flags = U2.ZERO;
    protected List<ConstantPoolInfo> constantPool;
    protected PoolReference this_class, super_class = PoolReference.ZERO;
    protected List<PoolReference> interfaces; //interfaces_count
    protected List<FieldInfo> fields; //fields_count
    protected List<MethodInfo> methods; //methods_count
    protected List<AttributeInfo> attributes; //attributes_count

    public ClassFileBuilder(int majorVersion, int minorVersion) {
        this.magic = MAGIC;
        this.major_version = U2.valueOf(majorVersion);
        this.minor_version = U2.valueOf(minorVersion);
        this.constantPool = new LinkedList<>();
        this.interfaces = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.helper().setSuperType(Type.OBJECT);
    }

    protected U2 interfacesCount() {
        return U2.valueOf(interfaces.size());
    }

    protected U2 fieldsCount() {
        return U2.valueOf(fields.size());
    }

    protected U2 methodsCount() {
        return U2.valueOf(methods.size());
    }

    protected U2 attributesCount() {
        return U2.valueOf(attributes.size());
    }

    public ClassFileBuilder setAccessFlags(Access.Type... flags) {
        this.access_flags = Access.of(flags).constant();
        return this;
    }

    public ClassFileBuilder addAccessFlags(Access.Type... flags) {
        this.access_flags = U2.valueOf(this.access_flags.value() | Access.of(flags).value());
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

    public ClassFile build() throws ClassBuilderException {
        if (this_class == null) throw new ClassBuilderException("Type was null.");
        if (super_class == null) throw new ClassBuilderException("Supertype was null.");
        return new ClassFile(magic, minor_version, major_version, constantPoolCount(), constantPool(), access_flags,
            this_class, super_class, interfacesCount(), interfaces.toArray(new PoolReference[0]), fieldsCount(),
            fields.toArray(new FieldInfo[0]), methodsCount(), methods.toArray(new MethodInfo[0]), attributesCount(),
            attributes.toArray(new AttributeInfo[0]));
    }

    public Helper helper() {
        return this.new Helper();
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
            return new ReferenceInfo(tag, this.constant(TYPE, member.owner()), this.constant(NAME_AND_TYPE,
                member.signature()));
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

        public <Klass extends java.lang.reflect.Type & TypeDescriptor> void setType(Klass type) {
            this_class = this.constant(TYPE, Type.of(type));
        }

        public <Klass extends java.lang.reflect.Type & TypeDescriptor> void setSuperType(Klass type) {
            super_class = this.constant(TYPE, Type.of(type));
        }

        public void removeSuperType() {
            super_class = this.constant(TYPE, Type.OBJECT);
        }

        @SafeVarargs
        public final <Klass extends java.lang.reflect.Type & TypeDescriptor> void addInterfaces(Klass... interfaces) {
            for (Klass klass : interfaces) ClassFileBuilder.this.interfaces.add(this.constant(TYPE, Type.of(klass)));
        }

    }

}
