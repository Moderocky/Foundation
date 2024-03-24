package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.assembler.attribute.BootstrapMethods;
import mx.kenzie.foundation.assembler.constant.ConstantPoolInfo;

import java.lang.constant.Constable;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;

public class BootstrapTableBuilder implements AttributeBuilder {

    private final ClassFileBuilder.Storage storage;
    protected final List<BootstrapMethods.BootstrapMethod> methods;

    protected BootstrapTableBuilder(ClassFileBuilder.Storage storage) {
        this.storage = storage;
        this.methods = new ArrayList<>();
    }

    public boolean isUsed() {
        return !methods.isEmpty();
    }

    public void registerMethod(MethodHandle handle, Constable... arguments) {
        this.methods.add(new BootstrapMethods.BootstrapMethod(storage.constant(ConstantPoolInfo.METHOD_HANDLE, handle)));
    }

    @Override
    public BootstrapMethods build() {
        return new BootstrapMethods(storage);
    }

    @Override
    public void finalise() {

    }

}
