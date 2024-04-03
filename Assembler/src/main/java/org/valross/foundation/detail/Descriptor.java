package org.valross.foundation.detail;

import org.valross.constantine.Constantive;
import org.valross.constantine.RecordConstant;

import java.lang.invoke.TypeDescriptor;

/**
 * A descriptor, used for method types, field types, etc.
 */
public interface Descriptor extends TypeDescriptor, Constantive {

    static Descriptor of(String string) {
        return new Method(string);
    }

    @SafeVarargs
    static <Klass extends java.lang.reflect.Type & TypeDescriptor> Descriptor of(Klass returnType,
                                                                                 Klass... parameters) {
        return new Method(Type.methodDescriptor(returnType, parameters));
    }

    record Method(String descriptorString) implements RecordConstant, Descriptor {

    }

}
