package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.assembler.attribute.AttributeInfo;
import mx.kenzie.foundation.assembler.attribute.Code;
import mx.kenzie.foundation.assembler.code.Branch;
import mx.kenzie.foundation.assembler.code.CodeElement;
import mx.kenzie.foundation.assembler.code.CodeVector;
import mx.kenzie.foundation.assembler.code.UnboundedElement;
import mx.kenzie.foundation.assembler.error.IncompatibleBranchError;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.detail.Type;
import mx.kenzie.foundation.detail.TypeHint;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public class CodeBuilder extends AttributableBuilder implements AttributeBuilder {

    protected final ClassFileBuilder.Storage storage;
    protected final MethodBuilder method;
    protected final PoolReference attributeName;
    private final StackTracker tracker = new StackTracker();
    private CodeVector vector;
    private int maxStack;
    private int maxLocals;
    private boolean trackStack = true, trackFrames = true;

    public CodeBuilder(MethodBuilder builder) {
        this.storage = builder.helper();
        this.method = builder;
        this.attributeName = storage.constant(UTF8, "Code");
    }

    protected CodeBuilder writingTo(CodeVector vector) {
        this.vector = vector;
        return this;
    }

    /**
     * @return The code vector that actually stores the current block.
     */
    protected CodeVector getVector() {
        return vector;
    }

    /**
     * Set the number of local variable slots being used by this method.
     * Note that wide types (long, double) each take up two slots.
     * The variable pool cannot exceed this number (e.g. you can't astore 9 unless the register has at least 10 slots)
     * but the register size may exceed the number of variables actually being used (e.g. you could allocate 2 slots
     * but use only slot 0).
     * This is automatically known based on the number of method parameters and whenever a store instruction is used.
     *
     * @param slots The number of variable slots available
     * @return Builder
     */
    public CodeBuilder registerSize(int slots) {
        this.maxLocals = slots;
        return this;
    }

    /**
     * Set the number of stack slots being used by this method.
     * Note that wide types (long, double) each take up two slots.
     * The number of items on the stack cannot exceed this number, but it is possible to allocate a higher stack size
     * than is actually used.
     * This is automatically tracked by default or if {@link #trackStack()} is true.
     *
     * @param slots The number of stack slots available
     * @return Builder
     */
    public CodeBuilder stackSize(int slots) {
        this.maxStack = slots;
        return this;
    }

    public CodeBuilder write(@NotNull UnboundedElement element) {
        final CodeElement bound = element.bound(storage);
        this.vector.append(bound);
        bound.insert(this);
        if (bound instanceof Branch branch && this.trackFrames()) {
            this.tracker.branches.add(branch);
            final var last = vector.getLast(2);
            final CodeElement previous = last.getFirst();
            if (previous instanceof Branch.UnconditionalBranch) {
                last.removeFirst();
                this.tracker.branches.remove(previous);
            } else if (previous instanceof Branch) {
                throw new IncompatibleBranchError("You have two branches at the same index " + vector.length());
            }
        }
        return this;
    }

    public CodeBuilder write(@NotNull UnboundedElement @NotNull ... elements) {
        for (UnboundedElement element : elements) this.write(element);
        return this;
    }

    public CodeBuilder write(byte... bytes) {
        return this.write(CodeElement.fixed(bytes));
    }

    public CodeBuilder insertAfter(@NotNull CodeElement target, @NotNull UnboundedElement element) {
        final CodeElement bound = element.bound(storage);
        this.vector.insertAfter(target, bound);
        bound.notify(this);
        if (bound instanceof Branch branch && this.trackFrames()) tracker.branches.add(branch);
        return this;
    }

    public CodeBuilder insertBefore(@NotNull CodeElement target, @NotNull UnboundedElement element) {
        final CodeElement bound = element.bound(storage);
        this.vector.insertBefore(target, bound);
        bound.notify(this);
        if (bound instanceof Branch branch && this.trackFrames()) tracker.branches.add(branch);
        return this;
    }

    @Contract(pure = true)
    public CodeBuilder write() {
        return this;
    }

    @Contract(pure = true)
    public MethodBuilder exit() {
        return method;
    }

    @Override
    public AttributeInfo build() {
        return Code.of(attributeName, U2.valueOf(maxStack), U2.valueOf(maxLocals), vector, new Code.Exception[0],
                       attributes(new AttributeInfo.CodeAttribute[0]));
    }

    public CodeBuilder attribute(AttributeInfo.CodeAttribute attribute) {
        return (CodeBuilder) super.attribute(attribute);
    }

    @Override
    public void finalise() {
        if (this.trackStack()) {
            this.stack().clear();
            this.register().clear();
            if (!Access.is(exit().access_flags, Access.STATIC)) {
                if (exit().name().equals("<init>")) // constructor starts with an uninitialised 'this'
                    this.register().put(0, TypeHint.uninitialisedThis(method.exit()));
                else this.register().put(0, method.exit());
            }
            for (Type parameter : this.exit().parameters()) this.register().putNext(parameter);
            try {
                for (CodeElement element : this.vector) element.notify(this);
            } catch (IncompatibleBranchError ex) {
                throw ex.setVector(vector);
            } catch (UnsupportedOperationException ex) {
                throw new IncompatibleBranchError(ex).setVector(vector);
            }
            this.maxStack = this.stack().maximum();
            this.maxLocals = this.register().maximum();
        }
        frames:
        if (this.trackFrames()) {
            if (!vector.isEmpty() && vector.getLast(1) instanceof Branch branch) {
                this.vector.getLast(1).remove(branch);
                this.tracker.branches.remove(branch);
            }
            if (tracker.branches.isEmpty() || !(tracker.branches.getFirst() instanceof Branch.ImplicitBranch))
                this.tracker.branches.addFirst(new Branch.ImplicitBranch(this.exit().parameters()));
            if (this.tracker.branches.size() == 1) break frames;
            this.tracker.builder = new StackMapTableBuilder(this.helper());
            Branch previous = null;
            for (Branch current : this.tracker.branches) {
                if (previous != null) this.tracker.builder.addFrame(this.helper(), previous, current);
                previous = current;
            }
            this.attributes.removeIf(attribute -> attribute instanceof StackMapTableBuilder);
            this.attributes.add(tracker.builder);
        }
        for (AttributeBuilder attribute : attributes) attribute.finalise();
    }

    @Override
    public ClassFileBuilder.Storage helper() {
        return storage;
    }

    /**
     * A model of the program's stack based on the instructions (and inputs) given.
     * This is used by element tracker to determine how to process context-dependent elements.
     *
     * @return The stack model
     */
    public ProgramStack stack() {
        return tracker.stack;
    }

    /**
     * A model of the program's register based on the instructions (and inputs) given.
     * This is used by element tracker to determine how to process context-dependent elements.
     *
     * @return The register model
     */
    public ProgramRegister register() {
        return tracker.register;
    }

    /**
     * Whether this builder wants to keep track of the stack size and composition.
     * If true, this builder supports context-driven instructions.
     *
     * @return If the builder is tracking the stack composition
     */
    public boolean trackStack() {
        return trackStack;
    }

    /**
     * Whether this builder wants to keep track of the frame composition.
     * Allows for automatic stack map frame calculation.
     *
     * @return If the builder is tracking the frame composition
     */
    public boolean trackFrames() {
        return trackFrames;
    }

    public CodeBuilder setTrackFrames(boolean trackFrames) {
        this.trackFrames = trackFrames;
        return this;
    }

    public CodeBuilder setTrackStack(boolean trackStack) {
        this.trackStack = trackStack;
        return this;
    }

    public CodeVector vector() {
        return vector;
    }

    protected static class StackTracker {

        private final ProgramStack stack = new ProgramStack();
        private final ProgramRegister register = new ProgramRegister();
        private final LinkedList<Branch> branches = new LinkedList<>();
        private StackMapTableBuilder builder;

    }

}
