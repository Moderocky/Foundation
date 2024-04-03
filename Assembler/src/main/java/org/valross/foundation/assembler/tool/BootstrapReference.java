package org.valross.foundation.assembler.tool;

import org.valross.foundation.assembler.attribute.BootstrapMethods;

import java.lang.ref.WeakReference;

public class BootstrapReference extends TableReference<BootstrapMethods.BootstrapMethod> {

    public BootstrapReference(Iterable<BootstrapMethods.BootstrapMethod> pool, BootstrapMethods.BootstrapMethod value) {
        super(pool, new WeakReference<>(value));
    }

    @Override
    public int index() {
        final BootstrapMethods.BootstrapMethod info = reference.get();
        if (info == null) return -1; // it was discarded :(
        int index = 0;
        for (BootstrapMethods.BootstrapMethod value : pool) {
            if (info == value) return index; // we want to know if it's EXACTLY our reference
            ++index;
        }
        return -1; // was it ever there in the first place?
    }

    @Override
    public String toString() {
        return "BootstrapReference[" + "index=" + this.index() + ", reference=" + reference.get() + ']';
    }

}


