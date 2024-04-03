package org.valross.foundation.assembler.tool;

import org.valross.foundation.detail.TypeHint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A special register implementation used for imitating the program's execution.
 * See {@link CodeBuilder#trackStack()}.
 * This class violates some conventions of the collection and stack classes (e.g. sizes) in order to better mimic
 * the real stack behaviour. As such, this should not be used as a real collection.
 */
public class ProgramRegister implements Iterable<TypeHint> {

    /**
     * Whether this contains any wide types and requires special calculation.
     */
    private final List<TypeHint> locals;
    protected int dirty;
    protected int maximum = 0;

    public ProgramRegister() {
        this.locals = new ArrayList<>();
    }

    protected boolean isDirty() {
        return dirty > 0;
    }

    /**
     * @return The types in their actual stack slots (i.e. wide types are followed by a null space)
     */
    public TypeHint @NotNull [] toWideArray() {
        return locals.toArray(new TypeHint[0]);
    }

    public void put(int slot, TypeHint hint) {
        final int extra = hint.width() - 1;
        while (locals.size() <= slot + extra) locals.add(TypeHint.top());
        this.locals.set(slot, hint);
        if (extra > 0) for (int i = 1; i <= extra; ++i) locals.set(slot + i, null);
        this.maximum = Math.max(maximum, slot + hint.width());
    }

    public void remove(int slot) {
        if (slot >= locals.size()) return;
        if (slot == locals.size() - 1) locals.removeLast();
        final TypeHint current = this.get(slot);
        for (int i = 0; i < current.width(); i++) this.put(slot + i, TypeHint.top());
    }

    public int size() {
        return locals.size() + dirty;
    }

    public int maximum() {
        return maximum;
    }

    public boolean isEmpty() {
        return locals.isEmpty();
    }

    public boolean contains(Object o) {
        return locals.contains(o);
    }

    @NotNull
    @Override
    public Iterator<TypeHint> iterator() {
        return locals.iterator();
    }

    public TypeHint @NotNull [] toArray() {
        final List<TypeHint> list = new ArrayList<>(locals.size());
        for (TypeHint local : locals) if (local != null) list.add(local);
        return list.toArray(new TypeHint[0]);
    }

    public void clear() {
        this.maximum = 0;
        this.locals.clear();
    }

    private void add(TypeHint hint) {
        this.maximum = Math.max(maximum, locals.size() + hint.width());
        this.locals.add(hint);
        for (int i = 1; i < hint.width(); ++i) locals.add(null);
    }

    public void reframe(TypeHint... register) {
        this.locals.clear();
        for (TypeHint hint : register) this.add(hint);
    }

    public TypeHint get(int slot) {
        if (slot >= locals.size()) throw new IllegalArgumentException("No variable is stored in " + slot);
        return locals.get(slot);
    }

    public void putNext(TypeHint type) {
        this.put(this.size(), type);
    }

    @Override
    public String toString() {
        return "ProgramRegister[" +
            "locals=" + locals +
            ", maximum=" + maximum +
            ']';
    }

    public void notifyMaximum(int slot) {
        this.maximum = Math.max(maximum, slot);
    }

}
