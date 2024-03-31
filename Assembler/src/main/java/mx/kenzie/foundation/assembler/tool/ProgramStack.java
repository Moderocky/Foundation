package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.detail.Erasure;
import mx.kenzie.foundation.detail.TypeHint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * A special stack implementation used for imitating the program's execution.
 * See {@link CodeBuilder#trackStack()}.
 * This class violates some conventions of the collection and stack classes (e.g. sizes) in order to better mimic
 * the real stack behaviour. As such, this should not be used as a real collection.
 */
public class ProgramStack extends Stack<TypeHint> {

    /**
     * A dummy flag to represent Java's TOP type, since we can never make an actual class reference to TOP.
     * This is designed to fail if any operations are performed on it, since it doesn't represent a real class.
     */
    public static final TypeHint TOP = TypeHint.top(), UNINITIALISED_THIS = TypeHint.uninitialisedThis();

    /**
     * Whether this contains any wide types and requires special calculation.
     */
    protected int dirty;

    protected boolean isDirty() {
        return dirty > 0;
    }

    public void consume(Erasure erasure, boolean isStatic) {
        this.pop(erasure.parameters());
        if (!isStatic) this.pop();
        this.push(erasure.returnType());
    }

    private TypeHint[] pop(TypeHint[] types) {
        final List<TypeHint> popped = new ArrayList<>(types.length);
        try {
            for (TypeHint type : types) {
                final TypeHint found = super.pop();
                if (found.width() == 2) --dirty;
                popped.add(found);
                if (type.width() != found.width())
                    throw new IllegalArgumentException("Expected to pop a " + type.getTypeName() + " but found a " + found.getTypeName());
            }
        } catch (RuntimeException | Error ex) {
            for (TypeHint spoiled : popped.reversed()) {
                super.push(spoiled);
                if (spoiled.width() == 2) ++dirty;
            }
            throw ex;
        }
        return popped.toArray(new TypeHint[0]);
    }

    public void consume(Erasure erasure) {
        this.consume(erasure, false);
    }

    public TypeHint[] pop(int slots) {
        //<editor-fold desc="Pops N types worth <slots> slots off the stack."
        // defaultstate="collapsed">
        final List<TypeHint> list = new ArrayList<>(slots * 2);
        for (int i = 0; i < slots; i++) {
            final TypeHint type = super.pop();
            if (type.width() == 2) {
                --this.dirty;
                ++i;
            }
            list.add(type);
        }
        return list.toArray(new TypeHint[0]);
        //</editor-fold>
    }

    public TypeHint[] pop2() {
        final TypeHint top = this.peek(), second;
        //<editor-fold desc="Pops the top (2 narrow, 1 wide) types and returns them in stack order."
        // defaultstate="collapsed">
        return switch (top.width()) {
            case 1 -> {
                second = super.pop();
                if (second.width() == 2) { // we tried to duplicate half of a long
                    super.push(second);
                    super.push(top);
                    throw new UnsupportedOperationException("The type below the top of the stack is WIDE (" + this.peek()
                                                                                                                  .getTypeName() + "), cannot pop half");
                }
                yield new TypeHint[] {second, top};
            }
            case 2 -> {
                --this.dirty;
                yield new TypeHint[] {super.pop()};
            }
            default -> new TypeHint[] {};
        };
        //</editor-fold>
    }

    public void replace(TypeHint type) {
        final TypeHint top = super.pop();
        if (top.width() != type.width()) throw new UnsupportedOperationException("Can't switch narrow/wide types.");
    }

    /**
     * Swaps the top two (narrow) types.
     */
    public void swap() {
        final TypeHint top = super.pop(), second = super.pop();
        //<editor-fold desc="Make sure neither is wide." defaultstate="collapsed">
        if (top.width() == 2) {
            super.push(second);
            super.push(top);
            throw new UnsupportedOperationException("The type on top of the stack is WIDE (" + this.peek()
                                                                                                   .getTypeName() +
                                                        ") so cannot swap");
        }
        if (second.width() == 2) {
            super.push(second);
            super.push(top);
            throw new UnsupportedOperationException("The type below the top of the stack is WIDE (" + this.peek()
                                                                                                          .getTypeName() + ") so cannot swap");
        }
        //</editor-fold>
        super.push(top);
        super.push(second);
    }

    public void dup() {
        final TypeHint top = this.peek();
        switch (top.width()) {
            case 1 -> super.push(top);
            case 2 -> throw new UnsupportedOperationException("The type on top of the stack is WIDE (" + this.peek()
                                                                                                             .getTypeName() + ") so must use DUP2");
        }
    }

    public void dupX1() {
        final TypeHint top = super.pop(), second = super.pop();
        //<editor-fold desc="Make sure neither is wide." defaultstate="collapsed">
        if (top.width() == 2) {
            super.push(second);
            super.push(top);
            throw this.error(0);
        }
        if (second.width() == 2) {
            super.push(second);
            super.push(top);
            throw this.error(1);
        }
        //</editor-fold>
        //<editor-fold desc="Duplicate slot 0 below slot 1." defaultstate="collapsed">
        super.push(top);
        super.push(second);
        super.push(top);
        //</editor-fold>
    }

    public void dupX2() {
        final TypeHint top = super.pop(), second = super.pop(), third;
        //<editor-fold desc="Make sure top type is narrow." defaultstate="collapsed">
        if (top.width() == 2) {
            super.push(second);
            super.push(top);
            throw this.error(0);
        }
        //</editor-fold>
        if (second.width() == 2) {
            //<editor-fold desc="Duplicate top below second." defaultstate="collapsed">
            super.push(top);
            super.push(second);
            super.push(top);
            //</editor-fold>
        } else {
            third = super.pop();
            //<editor-fold desc="Make sure slots 1 & 2 are narrow." defaultstate="collapsed">
            if (third.width() == 2) {
                super.push(third);
                super.push(second);
                super.push(top);
                throw this.error(2);
            }
            //</editor-fold>
            //<editor-fold desc="Duplicate slot 0 below slot 2." defaultstate="collapsed">
            super.push(top);
            super.push(third);
            super.push(second);
            super.push(top);
            //</editor-fold>
        }
    }

    public void dup2() {
        final TypeHint first = super.pop(), second;
        //<editor-fold desc="Duplicate top (2 narrow, 1 wide) type(s)." defaultstate="collapsed">
        switch (first.width()) {
            case 1 -> {
                second = super.pop();
                if (second.width() == 2) { // we tried to duplicate half of a long
                    super.push(second);
                    super.push(first);
                    throw new UnsupportedOperationException("The type below the top of the stack is WIDE (" + this.peek()
                                                                                                                  .getTypeName() + "), cannot duplicate half");
                }
                super.push(second);
                super.push(first);
                super.push(second);
                super.push(first);
            }
            case 2 -> {
                super.push(first);
                super.push(first);
                ++this.dirty;
            }
        }
        //</editor-fold>
    }

    public void dup2X1() {
        final TypeHint first = super.pop(), second, third;
        //<editor-fold desc="Duplicate top (2 narrow, 1 wide) type(s), insert 3(2) slots down."
        // defaultstate="collapsed">
        switch (first.width()) {
            case 1 -> {
                second = super.pop();
                if (second.width() == 2) { // we tried to duplicate half of a long
                    super.push(second);
                    super.push(first);
                    throw this.error(1);
                }
                third = super.pop();
                if (third.width() == 2) { // we tried to split a long in 2
                    super.push(third);
                    super.push(second);
                    super.push(first);
                    throw this.error(2);
                }
                super.push(second);
                super.push(first);
                super.push(third);
                super.push(second);
                super.push(first);
            }
            case 2 -> {
                third = super.pop();
                if (third.width() == 2) { // we tried to split a long in 2
                    super.push(third);
                    super.push(first);
                    throw this.error(1);
                }
                super.push(first);
                super.push(third);
                super.push(first);
                ++this.dirty;
            }
        }
        //</editor-fold>
    }

    public void dup2X2() {
        //<editor-fold desc="Duplicate top (2 narrow, 1 wide) type(s), insert 4(2) slots down."
        // defaultstate="collapsed">
        final TypeHint[] copy = this.pop2(); // take off 2 to be copied
        final TypeHint[] skip = this.pop2(); // take off 2 to be skipped
        for (TypeHint type : copy) this.push(type); // put the copies in
        for (TypeHint type : skip) this.push(type); // put the skip back
        for (TypeHint type : copy) this.push(type); // put the original back
        //</editor-fold>
    }

    private RuntimeException error(int index) {
        //<editor-fold desc="Try to provide feedback based on the slot." defaultstate="collapsed">
        if (index == 0) return new UnsupportedOperationException("The type on top of the stack is WIDE (" + this.peek()
                                                                                                                .getTypeName() + ").");
        if (index == 1)
            return new UnsupportedOperationException("The type below the top of the stack is WIDE (" + this.peek()
                                                                                                           .getTypeName() + ").");
        return new UnsupportedOperationException("The type at index " + index + " is WIDE (" + this.peek()
                                                                                                   .getTypeName() +
                                                     ").");
        //</editor-fold>
    }

    @Override
    public TypeHint push(TypeHint item) {
        switch (item.width()) {
            case 0 -> throw new IllegalArgumentException("Cannot put a VOID type on the stack.");
            case 2 -> this.dirty++;
        }
        return super.push(item);
    }

    @Override
    public TypeHint pop() {
        return switch (this.peek().width()) {
            case 1 -> super.pop();
            case 2 -> throw new UnsupportedOperationException("The type on top of the stack is WIDE (" + this.peek()
                                                                                                             .getTypeName() + ") so must use POP2");
            default -> throw new IllegalStateException("Unexpected type " + this.peek() + " on stack.");
        };
    }

    /**
     * @return The types in their actual stack slots (i.e. wide types are followed by a null space)
     */
    public TypeHint @NotNull [] toWideArray() {
        final TypeHint[] types = new TypeHint[this.size()];
        int index = 0;
        for (TypeHint type : this) {
            types[index] = type;
            index += type.width();
        }
        return types;
    }

    @Override
    public int size() {
        return super.size() + dirty;
    }

    @Override
    public TypeHint @NotNull [] toArray() {
        final Object[] array = super.toArray();
        return Arrays.copyOf(array, array.length, TypeHint[].class);
    }

    /**
     * Re-frames the model from a snapshot.
     * This is used at a branch when we came from somewhere else with a different stack.
     *
     * @param snapshot A snapshot of the stack. This can be a wide or narrow array.
     */
    public void reframe(TypeHint... snapshot) {
        // This is used at a branch when we came from somewhere else with a different stack.
        super.clear();
        this.dirty = 0;
        for (TypeHint type : snapshot) {
            if (type == null) continue;
            super.push(type);
            if (type.width() == 2) ++dirty;
        }
    }

}
