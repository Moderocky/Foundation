package mx.kenzie.foundation.detail;

import mx.kenzie.foundation.assembler.code.CodeElement;
import mx.kenzie.foundation.assembler.code.CodeVector;
import mx.kenzie.foundation.assembler.tool.InstructionReference;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

import java.lang.reflect.Type;

/**
 * Something that resembles (or is indicative of) a Java class.
 * This might be: 1. the Type handle for an actual, loaded class, 2. a Type reference to an unloaded class,
 * 3. A theoretical but unknown type (e.g. TOP), 4. An un-initialised known type (e.g. NEW xyz...),
 * or 5. An un-initialised unknown type (THIS before super-constructor call).
 */
public interface TypeHint extends Descriptor, Type {

    static TypeHint top() {
        return Top.TOP;
    }

    static TypeHint uninitialisedThis() {
        return This.THIS;
    }

    static TypeHint uninitialised(mx.kenzie.foundation.detail.Type type, CodeElement allocation, CodeVector vector) {
        return new Uninitialised(type, new InstructionReference(vector, allocation));
    }

    static TypeHint uninitialised(mx.kenzie.foundation.detail.Type type, UVec offset) {
        return new Uninitialised(type, offset);
    }

    default int width() {
        return 1;
    }

    default boolean isPrimitive() {
        return false;
    }

    default boolean isInitialisedType() {
        return true;
    }

    default boolean isRealType() {
        return true;
    }

    default boolean isTypeKnown() {
        return true;
    }

    default mx.kenzie.foundation.detail.Type asType() {
        return mx.kenzie.foundation.detail.Type.of(this);
    }

    record Uninitialised(mx.kenzie.foundation.detail.Type type, UVec offset) implements TypeHint, RecordConstant {

        @Override
        public boolean isInitialisedType() {
            return false;
        }

        @Override
        public String descriptorString() {
            return type.descriptorString();
        }

        @Override
        public String getTypeName() {
            return type.getTypeName();
        }

        @Override
        public mx.kenzie.foundation.detail.Type constant() {
            return type;
        }

    }

    record Guess(int width, boolean isPrimitive, boolean isInitialisedType, boolean isRealType, boolean isTypeKnown,
                 String descriptorString, String getTypeName) implements TypeHint, RecordConstant {}

}

record This() implements TypeHint, RecordConstant {

    static final This THIS = new This();

    @Override
    public boolean isInitialisedType() {
        return false;
    }

    @Override
    public boolean isRealType() {
        return false;
    }

    @Override
    public String descriptorString() {
        return "this";
    }

}

record Top() implements TypeHint, RecordConstant {

    static final Top TOP = new Top();

    @Override
    public boolean isInitialisedType() {
        return false;
    }

    @Override
    public boolean isRealType() {
        return false;
    }

    @Override
    public boolean isTypeKnown() {
        return false;
    }

    @Override
    public String descriptorString() {
        return "T";
    }

}
