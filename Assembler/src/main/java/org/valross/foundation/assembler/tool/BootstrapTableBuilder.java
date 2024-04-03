package org.valross.foundation.assembler.tool;

import org.valross.foundation.assembler.attribute.BootstrapMethods;
import org.valross.foundation.assembler.constant.ConstantPoolInfo;
import org.valross.foundation.detail.Member;

import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.util.ArrayList;
import java.util.List;

public class BootstrapTableBuilder implements AttributeBuilder {

    protected final List<BootstrapMethods.BootstrapMethod> methods;
    private final ClassFileBuilder.Storage storage;

    protected BootstrapTableBuilder(ClassFileBuilder.Storage storage) {
        this.storage = storage;
        this.methods = new ArrayList<>();
    }

    public boolean isUsed() {
        return !methods.isEmpty();
    }

    public BootstrapReference registerMethod(Member.Invocation invocation, Object... arguments) {
        final PoolReference[] references = new PoolReference[arguments.length];
        for (int i = 0; i < references.length; i++) {
            references[i] = switch (arguments[i]) {
                case Constable constable -> storage.constant(constable);
                case ConstantDesc desc -> storage.constantFromDescription(desc);
                default ->
                    throw new IllegalArgumentException("Bootstrap argument " + arguments[i] + " doesn't resemble " +
                                                           "a " + "constant.");
            };
        }
        return this.registerMethod(invocation, references);
    }

    public BootstrapReference registerMethod(Member.Invocation invocation, PoolReference... arguments) {
        final BootstrapMethods.BootstrapMethod method;
        this.methods.add(method =
                             new BootstrapMethods.BootstrapMethod(storage.constant(ConstantPoolInfo.METHOD_HANDLE,
                                                                                   invocation), arguments));
        return new BootstrapReference(methods, method);
    }

    @Override
    public BootstrapMethods build() {
        return new BootstrapMethods(storage, methods.toArray(new BootstrapMethods.BootstrapMethod[0]));
    }

    @Override
    public void finalise() {

    }

}
