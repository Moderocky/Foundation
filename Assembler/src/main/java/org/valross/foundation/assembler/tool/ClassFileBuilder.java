package org.valross.foundation.assembler.tool;

import org.valross.foundation.assembler.ClassFile;
import org.valross.foundation.assembler.FieldInfo;
import org.valross.foundation.assembler.MethodInfo;
import org.valross.foundation.assembler.attribute.AttributeInfo;
import org.valross.foundation.assembler.constant.*;
import org.valross.foundation.assembler.error.ClassBuilderException;
import org.valross.foundation.assembler.vector.U1;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.detail.*;
import org.valross.constantine.Constantive;

import java.lang.constant.*;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.*;
import static org.valross.foundation.assembler.constant.MethodTypeReference.*;

public class ClassFileBuilder extends ModifiableBuilder implements Constantive, TypeHint {

    private static final U4 MAGIC = U4.valueOf(0xCAFEBABE);
    protected final U4 magic;
    protected final U2 minor_version, major_version;
    protected List<ConstantPoolInfo> constantPool;
    protected PoolReference this_class, super_class = PoolReference.ZERO;
    protected List<PoolReference> interfaces; //interfaces_count
    protected List<FieldBuilder> fields; //fields_count
    protected List<MethodBuilder> methods; //methods_count
    protected BootstrapTableBuilder bootstrapTable;
    private Storage storage;
    private TypeHint us;

    public ClassFileBuilder(int majorVersion, int minorVersion) {
        this.magic = MAGIC;
        this.major_version = U2.valueOf(majorVersion);
        this.minor_version = U2.valueOf(minorVersion);
        this.constantPool = new LinkedList<>();
        this.interfaces = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.setSuperType(Type.OBJECT);
    }

    public ClassFileBuilder(int majorVersion, Type type) {
        this(majorVersion, Version.RELEASE);
        this.setType(type);
    }

    public BootstrapTableBuilder bootstrapTable() {
        if (bootstrapTable == null) {
            this.bootstrapTable = new BootstrapTableBuilder(this.helper());
            super.attribute(bootstrapTable);
        }
        return bootstrapTable;
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
        this.finalise();
        final FieldInfo[] fields = fields();
        final MethodInfo[] methods = methods();
        final AttributeInfo[] attributes = attributes();
        // some of these put more things into the constant pool, so we need to make sure the pool is baked LAST
        final ConstantPoolInfo[] pool = constantPool();
        return new ClassFile(magic, minor_version, major_version, constantPoolCount(), pool, access_flags,
                             this_class, super_class, interfacesCount(), interfaces.toArray(new PoolReference[0]),
                             fieldsCount(),
                             fields, methodsCount(), methods, attributesCount(), attributes);
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
        this.us = Type.of(type);
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

    public MethodBuilder constructor() {
        final MethodBuilder builder = new MethodBuilder(this.helper());
        this.methods.add(builder);
        return builder.named("<init>");
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
    public Storage helper() {
        if (storage != null) return storage;
        return storage = this.new Storage();
    }

    public ClassFileBuilder attribute(AttributeInfo.TypeAttribute attribute) {
        return (ClassFileBuilder) super.attribute(attribute);
    }

    public ClassFileBuilder attribute(Function<Storage, AttributeInfo.TypeAttribute> attribute) {
        return (ClassFileBuilder) super.makeAttribute(attribute);
    }

    @Override
    public void finalise() {
        if (this_class == null) throw new ClassBuilderException("Type was null.");
        if (super_class == null) throw new ClassBuilderException("Supertype was null.");
        this.constantPool.sort(ConstantPoolInfo::compareTo); // so our innards can bake their references
        if (bootstrapTable != null) bootstrapTable.finalise();
        for (FieldBuilder field : fields) field.finalise();
        for (MethodBuilder method : methods) method.finalise();
        for (AttributeBuilder attribute : attributes) attribute.finalise();
    }

    @Override
    public String descriptorString() {
        return us.descriptorString();
    }

    @Override
    public String getTypeName() {
        return us.getTypeName();
    }

    public class Storage {

        public ClassFileBuilder source() {
            return ClassFileBuilder.this;
        }

        public PoolReference constantFromDescription(ConstantDesc description) {
            return switch (description) {
                case ClassDesc value -> this.constant(TYPE, Type.of(value));
                case MethodTypeDesc desc -> this.constant(METHOD_TYPE, Descriptor.of(desc.descriptorString()));
                case DirectMethodHandleDesc desc -> this.constant(METHOD_HANDLE, new Member.Invocation(desc));
                case DynamicConstantDesc<?> desc -> this.constant(DYNAMIC, DynamicReference.of(desc));
                case Constable constable -> this.constant(constable);
                case Number value -> this.constant(INTEGER, value.intValue());
            };
        }

        public PoolReference constant(Constable constable) {
            if (constable == null) return PoolReference.ZERO;
            return switch (constable) {
                case PoolReference reference -> reference;
                case Long j -> this.constant(LONG, j);
                case Double d -> this.constant(DOUBLE, d);
                case String value -> this.constant(STRING, value);
                case Float value -> this.constant(FLOAT, value);
                case Number value -> this.constant(INTEGER, value.intValue());
                case Character value -> this.constant(INTEGER, (int) value);
                case Boolean value -> this.constant(INTEGER, value ? 1 : 0);
                case Class<?> value -> this.constant(TYPE, Type.of(value));
                case Type value -> this.constant(TYPE, value);
                case Member value ->
                    this.constant(value.isField() ? FIELD_REFERENCE : value.owner().isKnownInterface() ?
                        INTERFACE_METHOD_REFERENCE : METHOD_REFERENCE, value);
                case Signature value -> this.constant(NAME_AND_TYPE, value);
                case Member.Invocation value -> this.constant(METHOD_HANDLE, value);
                case MethodType value -> this.constant(METHOD_TYPE, Descriptor.of(value.descriptorString()));
                case Descriptor value -> this.constant(METHOD_TYPE, value);
                case Constantive constant -> this.constant(DYNAMIC, DynamicReference.of(constant.constant()));
                default -> throw new IllegalArgumentException("Unhandled constant value: " + constable);
            };
        }

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

        public ConstantPoolInfo valueOf(Type value) {
            if (value.isPrimitive()) throw new IllegalArgumentException("Can't LDC a primitive class (" + value + ")");
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

        public DynamicInfo constantDynamic(Signature signature, Member.Invocation invocation, Object... arguments) {
            return new DynamicInfo(DYNAMIC, bootstrapTable().registerMethod(invocation, arguments),
                                   this.constant(NAME_AND_TYPE, signature));
        }

        public DynamicInfo invokeDynamic(Signature signature, Member.Invocation invocation, Object... arguments) {
            return new DynamicInfo(INVOKE_DYNAMIC, bootstrapTable().registerMethod(invocation, arguments),
                                   this.constant(NAME_AND_TYPE, signature));
        }

        public MethodHandleInfo valueOf(Member.Invocation invocation) {
            return new MethodHandleInfo(METHOD_HANDLE, invocation, U1.valueOf(invocation.type()),
                                        switch (invocation.type()) {
                                            case GET_FIELD, GET_STATIC, PUT_FIELD, PUT_STATIC ->
                                                this.constant(FIELD_REFERENCE, invocation.member());
                                            case INVOKE_INTERFACE ->
                                                this.constant(INTERFACE_METHOD_REFERENCE, invocation.member());
                                            case INVOKE_STATIC, INVOKE_SPECIAL ->
                                                this.constant(invocation.isInterface() ? INTERFACE_METHOD_REFERENCE :
                                                                  METHOD_REFERENCE,
                                                              invocation.member());
                                            case INVOKE_VIRTUAL, NEW_INVOKE_SPECIAL ->
                                                this.constant(METHOD_REFERENCE, invocation.member());
                                            default ->
                                                throw new IllegalArgumentException("Unknown dynamic instruction " + invocation.type());
                                        });
        }

        public DescriptorInfo valueOf(Descriptor descriptor) {
            return new DescriptorInfo(this.constant(UTF8, descriptor.descriptorString()));
        }

        public DynamicInfo valueOf(DynamicReference reference) {
            return switch (reference.type()) {
                case CONSTANT:
                    yield this.constantDynamic(reference.signature(), reference.invocation(), reference.arguments());
                case INVOCATION:
                    yield this.invokeDynamic(reference.signature(), reference.invocation(), reference.arguments());
            };
        }

    }

}
