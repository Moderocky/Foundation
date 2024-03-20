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

public class ClassFileBuilder extends ModifiableBuilder implements Constantive {

    private static final U4 MAGIC = U4.valueOf(0xCAFEBABE);
    protected final U4 magic;
    protected final U2 minor_version, major_version;
    protected List<ConstantPoolInfo> constantPool;
    protected PoolReference this_class, super_class = PoolReference.ZERO;
    protected List<PoolReference> interfaces; //interfaces_count
    protected List<FieldBuilder> fields; //fields_count
    protected List<MethodBuilder> methods; //methods_count
    protected List<AttributeInfo> attributes; //attributes_count
    private Storage storage;

    public ClassFileBuilder(int majorVersion, int minorVersion) {
        this.magic = MAGIC;
        this.major_version = U2.valueOf(majorVersion);
        this.minor_version = U2.valueOf(minorVersion);
        this.constantPool = new LinkedList<>();
        this.interfaces = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.setSuperType(Type.OBJECT);
    }

    public ClassFileBuilder(int majorVersion, Type type) {
        this(majorVersion, Version.RELEASE);
        this.setType(type);
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

    protected U2 constantPoolCount() {
        int size = 0;
        for (ConstantPoolInfo info : constantPool) size += info.tag().indices();
        return U2.valueOf(size + 1);
    }

    protected ConstantPoolInfo[] constantPool() {
        this.constantPool.sort(ConstantPoolInfo::compareTo);
        final List<ConstantPoolInfo> list = new ArrayList<>(constantPool.size() + 8); // guesswork
        list.add(new DeadSpaceInfo()); // the first index is empty
        for (ConstantPoolInfo info : constantPool) {
            list.add(info);
            if (info instanceof LongNumberInfo<?>) list.add(new DeadSpaceInfo());
        }
        return list.toArray(new ConstantPoolInfo[0]);
    }

    protected FieldInfo[] fields() {
        final List<FieldInfo> list = new ArrayList<>(fields.size());
        for (FieldBuilder value : fields) list.add(value.constant());
        return list.toArray(new FieldInfo[0]);
    }

    public ClassFile build() throws ClassBuilderException {
        if (this_class == null) throw new ClassBuilderException("Type was null.");
        if (super_class == null) throw new ClassBuilderException("Supertype was null.");
        return new ClassFile(magic, minor_version, major_version, constantPoolCount(), constantPool(), access_flags,
            this_class, super_class, interfacesCount(), interfaces.toArray(new PoolReference[0]), fieldsCount(),
            fields(), methodsCount(), methods(), attributesCount(), attributes.toArray(new AttributeInfo[0]));
    }

    private MethodInfo[] methods() {
        final List<MethodInfo> list = new ArrayList<>(methods.size());
        for (MethodBuilder value : methods) list.add(value.constant());
        return list.toArray(new MethodInfo[0]);
    }

    public ClassFileBuilder setModifiers(Access.Type... flags) {
        return (ClassFileBuilder) super.setModifiers(flags);
    }

    public ClassFileBuilder addModifiers(Access.Type... flags) {
        return (ClassFileBuilder) super.addModifiers(flags);
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> ClassFileBuilder setType(Klass type) {
        this.this_class = this.helper().constant(TYPE, Type.of(type));
        return this;
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> ClassFileBuilder setSuperType(Klass type) {
        if (type == null) return this.removeSuperType();
        this.super_class = this.helper().constant(TYPE, Type.of(type));
        return this;
    }

    public ClassFileBuilder removeSuperType() {
        this.super_class = this.helper().constant(TYPE, Type.OBJECT);
        return this;
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor> ClassFileBuilder addInterfaces(Klass... interfaces) {
        for (Klass klass : interfaces) this.interfaces.add(this.helper().constant(TYPE, Type.of(klass)));
        return this;
    }

    @Override
    public ClassFile constant() {
        return this.build();
    }

    public FieldBuilder field() {
        final FieldBuilder builder = new FieldBuilder(this.helper());
        this.fields.add(builder);
        return builder;
    }

    public MethodBuilder method() {
        final MethodBuilder builder = new MethodBuilder(this.helper());
        this.methods.add(builder);
        return builder;
    }

    @Override
    public ClassFileBuilder synthetic() {
        return (ClassFileBuilder) super.synthetic();
    }

    @Override
    public ClassFileBuilder deprecated() {
        return (ClassFileBuilder) super.deprecated();
    }

    @Override
    protected ClassFileBuilder attribute(AttributeInfo attribute) {
        return (ClassFileBuilder) super.attribute(attribute);
    }

    @Override
    public Storage helper() {
        if (storage != null) return storage;
        return storage = this.new Storage();
    }

    public class Storage {

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

        public ClassFileBuilder source() {
            return ClassFileBuilder.this;
        }

    }

}
