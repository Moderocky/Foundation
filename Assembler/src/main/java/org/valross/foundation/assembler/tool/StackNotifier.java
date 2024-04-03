package org.valross.foundation.assembler.tool;

import org.valross.foundation.detail.TypeHint;
import org.valross.constantine.RecordConstant;

import java.util.function.Consumer;

public record StackNotifier(Consumer<ProgramStack> consumer) implements Consumer<CodeBuilder>, RecordConstant {

    public static final StackNotifier POP = new StackNotifier(ProgramStack::pop);
    public static final StackNotifier POP2 = new StackNotifier(ProgramStack::pop2);
    public static final StackNotifier DUP = new StackNotifier(ProgramStack::dup);
    public static final StackNotifier DUP_X1 = new StackNotifier(ProgramStack::dupX1);
    public static final StackNotifier DUP_X2 = new StackNotifier(ProgramStack::dupX2);
    public static final StackNotifier DUP2 = new StackNotifier(ProgramStack::dup2);
    public static final StackNotifier DUP2_X1 = new StackNotifier(ProgramStack::dup2X1);
    public static final StackNotifier DUP2_X2 = new StackNotifier(ProgramStack::dup2X2);
    public static final StackNotifier SWAP = new StackNotifier(ProgramStack::swap);

    public static StackNotifier pop(int increment) {
        return new StackNotifier(stack -> stack.pop(Math.abs(increment)));
    }

    public static Consumer<CodeBuilder> pushVariable(int slot) {
        return builder -> {
            if (!builder.trackStack()) return;
            final TypeHint hint = builder.register().get(slot);
            builder.stack().push(hint);
        };
    }

    public static StackNotifier push(TypeHint type) {
        return new StackNotifier(stack -> stack.push(type));
    }

    public static StackNotifier pop2push(TypeHint type) {
        return new StackNotifier(stack -> {
            stack.pop2();
            stack.push(type);
        });
    }

    public static StackNotifier pop1push(TypeHint type) {
        return new StackNotifier(stack -> {
            stack.pop();
            stack.push(type);
        });
    }

    public static StackNotifier replace(TypeHint... types) {
        return new StackNotifier(stack -> {
            stack.pop(TypeHint.width(types));
            stack.push(types);
        });
    }

    @Override
    public void accept(CodeBuilder builder) {
        if (!builder.trackStack()) return;
        final int before = builder.stack().size();
        this.consumer.accept(builder.stack());
    }

}
