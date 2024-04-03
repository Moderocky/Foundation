package org.valross.foundation.assembler.constant;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.attribute.BootstrapMethods;
import org.valross.foundation.assembler.tool.BootstrapReference;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.foundation.detail.DynamicReference;
import org.valross.foundation.detail.Member;
import org.valross.foundation.detail.Signature;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

public record DynamicInfo(ConstantType<DynamicInfo, ?> tag, BootstrapReference bootstrap_method_attr_index,
                          PoolReference name_and_type_index) implements ConstantPoolInfo, Data, UVec, RecordConstant {

    @Override
    public UVec info() {
        return UVec.of(bootstrap_method_attr_index, name_and_type_index);
    }

    @Override
    public boolean is(Constable object) {
        return object instanceof DynamicReference reference
            && reference.type().ordinal() + 17 == tag().value().intValue()
            && name_and_type_index.ensure().is(reference.signature()) // we should check arguments properly
            && bootstrap_method_attr_index.ensure().bootstrap_arguments().length == reference.arguments().length // todo
            && bootstrap_method_attr_index.ensure().bootstrap_method_ref().ensure().is(reference.invocation());
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.tag().write(stream);
        this.bootstrap_method_attr_index.write(stream);
        this.name_and_type_index.write(stream);
    }

    @Override
    public int sort() {
        if (tag == ConstantPoolInfo.DYNAMIC) return 61;
        return 62;
    }

    @Override
    public DynamicReference unpack() {
        final DynamicReference.Kind kind = tag == ConstantPoolInfo.DYNAMIC
            ? DynamicReference.Kind.CONSTANT : DynamicReference.Kind.INVOCATION;
        final BootstrapMethods.BootstrapMethod ensure = bootstrap_method_attr_index.ensure();
        final Signature signature = ConstantPoolInfo.NAME_AND_TYPE.unpack(name_and_type_index.get());
        final Member.Invocation invocation = ConstantPoolInfo.METHOD_HANDLE.unpack(ensure.bootstrap_method_ref().get());
        final PoolReference[] references = ensure.bootstrap_arguments();
        final Object[] arguments = new Object[references.length];
        for (int i = 0; i < references.length; i++) arguments[i] = references[i].ensure().unpack();
        return new DynamicReference(kind, signature, invocation, arguments);
    }

    @Override
    public int length() {
        return 5;
    }

}
