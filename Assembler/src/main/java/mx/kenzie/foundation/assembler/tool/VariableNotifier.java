package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.detail.TypeHint;
import org.valross.constantine.RecordConstant;

import java.util.function.Consumer;

public record VariableNotifier(int slot) implements Consumer<CodeBuilder>, RecordConstant {

    @Override
    public void accept(CodeBuilder builder) {
        if (!builder.trackStack()) return;
        final TypeHint hint = builder.stack().popSafe();
        builder.register().put(slot, hint);
    }

}
