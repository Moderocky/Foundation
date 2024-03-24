package mx.kenzie.foundation.detail;

import mx.kenzie.foundation.assembler.tool.Access;

/**
 * An enum equivalent of the {@link Access} constants.
 * This is less efficient (and lacks the compile-time safety checks used for the assembler)
 * but comes with the enum behaviours e.g. switching.
 */
public enum Modifier implements Access, Access.All {
    PUBLIC(Access.PUBLIC), PROTECTED(Access.PROTECTED), PACKAGE_PRIVATE(0), PRIVATE(Access.PRIVATE),
    STATIC(Access.STATIC), FINAL(Access.FINAL), SUPER(Access.SUPER), SYNCHRONIZED(Access.SYNCHRONIZED),
    VOLATILE(Access.VOLATILE), BRIDGE(Access.BRIDGE), VARARGS(Access.VARARGS), TRANSIENT(Access.TRANSIENT),
    NATIVE(Access.NATIVE), INTERFACE(Access.INTERFACE), ABSTRACT(Access.ABSTRACT), STRICT(Access.STRICT),
    SYNTHETIC(Access.SYNTHETIC), ANNOTATION(Access.ANNOTATION), MANDATED(Access.MANDATED), ENUM(Access.ENUM),
    OPEN(0x0020), TRANSITIVE(0x0020), STATIC_PHASE(0x0040), MODULE(0x8000);

    public final Access access;
    public final int code;

    Modifier(Access access) {
        this.access = access;
        this.code = access.value();
    }

    Modifier(int code) {
        this.access = () -> code;
        this.code = code;
    }

    public Access access() {
        return access;
    }

    @Override
    public int value() {
        return code;
    }

}
