package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.UVec;

@FunctionalInterface
public interface Access extends UVec {

    All PUBLIC = () -> 0x0001; // Declared public; may be accessed from outside its package.
    All PRIVATE = () -> 0x0002; // Declared private; usable only within the defining class.
    All PROTECTED = () -> 0x0004; // Declared protected; may be accessed within subclasses.
    All STATIC = () -> 0x0008; // Declared static.
    TypeOrMethod ABSTRACT = () -> 0x0400; // Declared abstract; may not be instantiated.
    All FINAL = () -> 0x0010; // Declared final; no subclasses allowed; no further assignment after initialization.
    Type SUPER = () -> 0x0020; // Treat superclass methods specially when invoked by the invokespecial instruction.
    Type INTERFACE = () -> 0x0200; // Is an interface, not a class.
    Field VOLATILE = () -> 0x0040; // Declared volatile; cannot be cached.
    Method BRIDGE = () -> 0x0040; // This method directly calls another method.
    Field TRANSIENT = () -> 0x0080; // Declared transient; not written or read by a persistent object manager.
    Method VARARGS = () -> 0x0080; // The last (array) parameter can take any number of elements.
    Method SYNCHRONIZED = () -> 0x0020; // Declared synchronized; invocation is wrapped in a monitor lock.
    Method NATIVE = () -> 0x0100; // Declared native; implemented in a language other than Java.
    Method STRICT = () -> 0x0800; // Floating-point mode is FP-strict
    All SYNTHETIC = () -> 0x1000; // This wasn't in the source code
    Type ANNOTATION = () -> 0x2000; // This is an annotation (@interface)
    All MANDATED = () -> 0x8000; // This wasn't in the source code but the spec said I had to
    Type RECORD = () -> 0x10000; // For a Record (type)
    Type ENUM = () -> 0x4000; // For an Enum (type)

    static boolean is(UVec flags, Access check) {
        return (flags.intValue() & check.intValue()) != 0;
    }

    static All of(All... flags) {
        int i = 0;
        for (Access flag : flags) i |= flag.value();
        final int value = i;
        return () -> value;
    }

    static Type of(Type... flags) {
        int i = 0;
        for (Access flag : flags) i |= flag.value();
        final int value = i;
        return () -> value;
    }

    static Field of(Field... flags) {
        int i = 0;
        for (Access flag : flags) i |= flag.value();
        final int value = i;
        return () -> value;
    }

    static Method of(Method... flags) {
        int i = 0;
        for (Access flag : flags) i |= flag.value();
        final int value = i;
        return () -> value;
    }

    static Access of(Access... flags) {
        int i = 0;
        for (Access flag : flags) i |= flag.value();
        final int value = i;
        return () -> value;
    }

    default @Override int length() {
        return 2;
    }

    @Override
    default byte[] binary() {
        return this.constant().binary();
    }

    int value();

    @Override
    default U2 constant() {
        return U2.valueOf(this.value());
    }

    interface Type extends Access {}

    interface Field extends Access {}

    interface Method extends Access {}

    interface TypeOrMethod extends Type, Method {}

    interface All extends Type, Field, Method, TypeOrMethod {}

}
