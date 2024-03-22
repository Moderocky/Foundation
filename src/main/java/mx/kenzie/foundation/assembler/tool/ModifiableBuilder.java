package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.assembler.U2;

abstract class ModifiableBuilder extends AttributableBuilder {

    protected U2 access_flags = U2.ZERO;

    ModifiableBuilder() {

    }

    public U2 modifiers() {
        return access_flags;
    }

    public boolean hasModifier(Access flag) {
        return Access.is(access_flags, flag);
    }

    protected ModifiableBuilder setModifiers(Access... flags) {
        this.access_flags = Access.of(flags).constant();
        return this;
    }

    protected ModifiableBuilder addModifiers(Access... flags) {
        this.access_flags = U2.valueOf(this.access_flags.value() | Access.of(flags).value());
        return this;
    }

}
