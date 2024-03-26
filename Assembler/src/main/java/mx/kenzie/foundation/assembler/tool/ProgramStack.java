package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.detail.Erasure;
import mx.kenzie.foundation.detail.Type;
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
public class ProgramStack extends Stack<Type> {

    /**
     * A dummy flag to represent Java's TOP type, since we can never make an actual class reference to TOP.
     * This is designed to fail if any operations are performed on it, since it doesn't represent a real class.
     */
    public static final Type TOP = new Type(null, null, null);

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

    private Type[] pop(Type[] types) {
        final List<Type> popped = new ArrayList<>(types.length);
        try {
            for (Type type : types) {
                final Type found = super.pop();
                if (found.width() == 2) --dirty;
                popped.add(found);
                if (type.width() != found.width())
                    throw new IllegalArgumentException("Expected to pop a " + type.getTypeName() + " but found a " + found.getTypeName());
            }
        } catch (RuntimeException | Error ex) {
            for (Type spoiled : popped.reversed()) {
                super.push(spoiled);
                if (spoiled.width() == 2) ++dirty;
            }
            throw ex;
        }
        return popped.toArray(new Type[0]);
    }

    public void consume(Erasure erasure) {
        this.consume(erasure, false);
    }

    public Type[] pop(int slots) {
        //<editor-fold desc="Pops N types worth <slots> slots off the stack."
        // defaultstate="collapsed">
        final List<Type> list = new ArrayList<>(slots * 2);
        for (int i = 0; i < slots; i++) {
            final Type type = super.pop();
            if (type.width() == 2) {
                --this.dirty;
                ++i;
            }
            list.add(type);
        }
        return list.toArray(new Type[0]);
        //</editor-fold>
    }

    public Type[] pop2() {
        final Type top = this.peek(), second;
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
                yield new Type[] {second, top};
            }
            case 2 -> {
                --this.dirty;
                yield new Type[] {super.pop()};
            }
            default -> new Type[] {};
        };
        //</editor-fold>
    }

    public void replace(Type type) {
        final Type top = super.pop();
        if (top.width() != type.width()) throw new UnsupportedOperationException("Can't switch narrow/wide types.");
    }

    /**
     * Swaps the top two (narrow) types.
     */
    public void swap() {
        final Type top = super.pop(), second = super.pop();
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
        final Type top = this.peek();
        switch (top.width()) {
            case 1 -> super.push(top);
            case 2 -> throw new UnsupportedOperationException("The type on top of the stack is WIDE (" + this.peek()
                                                                                                             .getTypeName() + ") so must use DUP2");
        }
    }

    public void dupX1() {
        final Type top = super.pop(), second = super.pop();
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
        final Type top = super.pop(), second = super.pop(), third;
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
        final Type first = super.pop(), second;
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
        final Type first = super.pop(), second, third;
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
        final Type[] copy = this.pop2(); // take off 2 to be copied
        final Type[] skip = this.pop2(); // take off 2 to be skipped
        for (Type type : copy) this.push(type); // put the copies in
        for (Type type : skip) this.push(type); // put the skip back
        for (Type type : copy) this.push(type); // put the original back
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
    public Type push(Type item) {
        switch (item.width()) {
            case 0 -> throw new IllegalArgumentException("Cannot put a VOID type on the stack.");
            case 2 -> this.dirty++;
        }
        return super.push(item);
    }

    @Override
    public Type pop() {
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
    public Type @NotNull [] toWideArray() {
        final Type[] types = new Type[this.size()];
        int index = 0;
        for (Type type : this) {
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
    public Type @NotNull [] toArray() {
        final Object[] array = super.toArray();
        return Arrays.copyOf(array, array.length, Type[].class);
    }

}
