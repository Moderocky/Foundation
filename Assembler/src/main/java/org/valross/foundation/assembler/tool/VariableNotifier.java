package org.valross.foundation.assembler.tool;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.detail.TypeHint;

import java.util.function.Consumer;

public record VariableNotifier(int slot) implements Consumer<CodeBuilder>, RecordConstant {

    @Override
    public void accept(CodeBuilder builder) {
        if (!builder.trackStack()) return;
        final TypeHint hint = builder.stack().popSafe();
        builder.register().put(slot, hint);
    }

}
