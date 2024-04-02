package mx.kenzie.foundation.assembler.constant;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.vector.UVec;
import mx.kenzie.foundation.detail.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

/**
 * An entry into the constant pool, such as a primitive (number) value, a type, field or method name.
 * Very few data types can be entered here, but complex values can be constructed from smaller values already in
 * the constant pool.
 * As a result, for some cases it may be important how the pool is ordered
 * (e.g. we want a method's name to be in the pool before the method handle) and so constant pool entries have a
 * 'sort' code to decide where they go in. Smaller sort codes are entered before larger sort codes.
 * For simplicity, values that depends on others ought to have higher sort codes.
 */
public interface ConstantPoolInfo extends Data, Comparable<ConstantPoolInfo> {

    ConstantType<Utf8Info, String> UTF8 = new ConstantType<>(1, Utf8Info.class, String.class,
                                                             ClassFileBuilder.Storage::valueOf);
    ConstantType<ConstantPoolInfo, Type> TYPE = new ConstantType<>(7, ConstantPoolInfo.class, Type.class,
                                                            ClassFileBuilder.Storage::valueOf);
    ConstantType<StringInfo, String> STRING = new ConstantType<>(8, StringInfo.class, String.class,
                                                                 ClassFileBuilder.Storage::valueOfString);
    ConstantType<SignatureInfo, Signature> NAME_AND_TYPE = new ConstantType<>(12, SignatureInfo.class,
                                                                              Signature.class,
                                                                              ClassFileBuilder.Storage::valueOf);
    ConstantType<DescriptorInfo, Descriptor> METHOD_TYPE = new ConstantType<>(16, DescriptorInfo.class,
                                                                              Descriptor.class,
                                                                              ClassFileBuilder.Storage::valueOf);

    ConstantType<?, ?> tag();

    UVec info();    ConstantType<ReferenceInfo, Member> METHOD_REFERENCE = new ConstantType<>(10, ReferenceInfo.class, Member.class,
                                                                              ClassFileBuilder.Storage::valueOfMethod);

    /**
     * Whether this is (probably) storing the given object.
     * This should use a value-based equality check, since the aim is to
     * prevent duplicates being added to the constant pool.
     */
    boolean is(Constable object);

    @Override
    default void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.tag().write(stream);
        this.info().write(stream);
    }    ConstantType<ReferenceInfo, Member> INTERFACE_METHOD_REFERENCE = new ConstantType<>(11, ReferenceInfo.class,
                                                                                        Member.class,
                                                                                        ClassFileBuilder.Storage::valueOfInterfaceMethod);

    @Override
    default byte[] binary() { // inefficient but subclasses should deal with this
        return UVec.of(this.tag(), this.info()).binary();
    }

    /**
     * @return The sort code of this constant.
     */
    default int sort() {
        return 99;
    }    @SuppressWarnings({"unchecked", "RawUseOfParameterized"})
    ConstantType<NumberInfo<Integer>, Integer> INTEGER = new ConstantType<>(3,
                                                                            (Class<NumberInfo<Integer>>) (Class) NumberInfo.class, Integer.class, ClassFileBuilder.Storage::valueOf);

    default @Override int compareTo(@NotNull ConstantPoolInfo o) {
        return Integer.compare(this.sort(), o.sort());
    }

    ConstantType<DynamicInfo, DynamicReference> DYNAMIC = new ConstantType<>(17, DynamicInfo.class,
                                                                             DynamicReference.class,
                                                                             ClassFileBuilder.Storage::valueOf);



    ConstantType<DynamicInfo, DynamicReference> INVOKE_DYNAMIC = new ConstantType<>(18, DynamicInfo.class,
                                                                                    DynamicReference.class,
                                                                                    ClassFileBuilder.Storage::valueOf);



    @SuppressWarnings({"unchecked", "RawUseOfParameterized"})
    ConstantType<NumberInfo<Float>, Float> FLOAT = new ConstantType<>(4,
                                                                      (Class<NumberInfo<Float>>) (Class) NumberInfo.class, Float.class, ClassFileBuilder.Storage::valueOf);



    @SuppressWarnings({"unchecked", "RawUseOfParameterized"})
    ConstantType<LongNumberInfo<Long>, Long> LONG = new ConstantType<>(5,
                                                                       (Class<LongNumberInfo<Long>>) (Class) LongNumberInfo.class, Long.class, ClassFileBuilder.Storage::valueOf);
    @SuppressWarnings({"unchecked", "RawUseOfParameterized"})
    ConstantType<LongNumberInfo<Double>, Double> DOUBLE = new ConstantType<>(6,
                                                                             (Class<LongNumberInfo<Double>>) (Class) LongNumberInfo.class, Double.class, ClassFileBuilder.Storage::valueOf);
    ConstantType<ReferenceInfo, Member> FIELD_REFERENCE = new ConstantType<>(9, ReferenceInfo.class, Member.class,
                                                                             ClassFileBuilder.Storage::valueOfField);
    ConstantType<MethodHandleInfo, Member.Invocation> METHOD_HANDLE = new ConstantType<>(15, MethodHandleInfo.class,
                                                                                         Member.Invocation.class,
                                                                                         ClassFileBuilder.Storage::valueOf);

}

