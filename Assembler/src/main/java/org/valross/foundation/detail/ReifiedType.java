package org.valross.foundation.detail;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Type;

/**
 * Represents a type (e.g. a class that may or may not exist) for which the serialised bytecodes
 * are known and available in the machine.
 * This is usually a created class which can be loaded, or possibly something having been loaded.
 */
public interface ReifiedType extends TypeHint, TypeDescriptor, Type {

    byte[] bytecode();

}
