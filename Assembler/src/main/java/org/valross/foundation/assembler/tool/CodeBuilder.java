package org.valross.foundation.assembler.tool;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.valross.foundation.assembler.attribute.AttributeInfo;
import org.valross.foundation.assembler.attribute.Code;
import org.valross.foundation.assembler.code.*;
import org.valross.foundation.assembler.error.IncompatibleBranchError;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.detail.Type;
import org.valross.foundation.detail.TypeHint;

import java.util.LinkedList;

import static org.valross.foundation.assembler.constant.ConstantPoolInfo.UTF8;

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
            if (vector.length() < 2)
                return this; // Can't do look-back yet
            final var last = vector.getLast(2);
            final CodeElement previous = last.getFirst();
            if (previous instanceof Branch.UnconditionalBranch
                || previous instanceof Branch.UnreachableBranch) {
                last.removeFirst();
                this.tracker.branches.remove(previous);
            } else if (previous instanceof Branch) {
                // Could re-use the branch, but it's easier to spot a calculation error
                this.vector.insertBefore(bound, OpCode.NOP);
//                throw new IncompatibleBranchError("You have two branches at the same index " + vector.length());
            }
        } else if (this.trackFrames() && sinceLastBranch() > 12) check: {
            // Branches can't be more than 63 bytes apart because the offsets are encoded in the branch type byte
            // for some ridiculous reason
            // We also don't want to insert a branch between a new instance and its constructor call
            // because we can't safely track its use
            int uninitialised = 0;
            for (CodeElement codeElement : vector.getLast(11)) {
                if (codeElement.code() == Codes.NEW) uninitialised++;
                else if (codeElement.code() == Codes.INVOKESPECIAL && uninitialised > 0) uninitialised--;
            }
            if (uninitialised > 0) break check;
            return this.write(new Branch());
        }
        return this;
    }

    private int sinceLastBranch() {
        int last = 0;
        for (CodeElement codeElement : this.vector) {
            if (codeElement instanceof Branch) last = 0;
            else ++last;
        }
        return last;
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

    private void checkMissingBranch(CodeElement element) {
        switch (element.code()) {
            case Codes.ARETURN, Codes.RETURN, Codes.RET, Codes.IRETURN, Codes.FRETURN, Codes.LRETURN, Codes.DRETURN:
                break;
            default:
                return;
        }
        CodeElement after = this.vector.getAfter(element);
        if (after == null) return;
        if (!(after instanceof Branch)) {
            Branch branch = new Branch.UnreachableBranch();
            this.vector.insertAfter(element, branch);
            int index = 0;
            for (CodeElement code : vector) {
                if (code == branch) break;
                if (code instanceof Branch)
                    index++;
            }
            this.tracker.branches.add(index, branch);
        }
    }

    @Override
    public void finalise() {
        if (this.trackStack()) {
            this.stack().clear();
            this.register().clear();
            if (!Access.is(exit().access_flags, Access.STATIC)) {
                if (exit().name().equals("<init>")) // constructor starts with an uninitialised 'this'
                    this.register().put(0, TypeHint.uninitialisedThis(method.exit()));
                else this.register().put(0, method.exit().asType());
            }
            for (Type parameter : this.exit().parameters()) this.register().putNext(parameter);
            int index = 0;
            Branch branch = new Branch.UnconditionalBranch();
            for (CodeElement element : this.vector) {
                try {
                    this.checkMissingBranch(element);
                    if (element instanceof Branch b) branch = b;
                    element.notify(this);
                    index += element.length();
                } catch (IncompatibleBranchError ex) {
                    throw ex.setVector(vector).setBranch(branch).setProblem(element).setIndex(index);
                } catch (UnsupportedOperationException | IllegalArgumentException ex) {
                    throw new IncompatibleBranchError(ex)
                        .setBranch(branch)
                        .setProblem(element)
                        .setIndex(index)
                        .setVector(vector);
                }
            }
            this.maxStack = this.stack().maximum();
            this.maxLocals = this.register().maximum();
        }
        frames:
        if (this.trackFrames()) {
            while (!vector.isEmpty() && vector.getLast(1).getLast() instanceof Branch branch) {
                this.vector.getLast(1).remove(branch);
                this.tracker.branches.remove(branch);
            }
            if (tracker.branches.isEmpty() || !(tracker.branches.getFirst() instanceof Branch.ImplicitBranch))
                this.tracker.branches.addFirst(this.openingRegister());
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

    private Branch openingRegister() {
        // return the correct opening register for non-static methods
        MethodBuilder builder = this.exit();
        Type[] parameters = builder.parameters();
        if (builder.hasModifier(Access.STATIC))
            return new Branch.ImplicitBranch(parameters);
        Type[] register = new Type[parameters.length + 1];
        register[0] = builder.exit().asType();
        System.arraycopy(parameters, 0, register, 1, parameters.length);
        return new Branch.ImplicitBranch(register);
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
