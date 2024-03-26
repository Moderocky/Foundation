package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.assembler.attribute.AttributeInfo;
import mx.kenzie.foundation.assembler.attribute.Code;
import mx.kenzie.foundation.assembler.code.CodeElement;
import mx.kenzie.foundation.assembler.code.CodeVector;
import mx.kenzie.foundation.assembler.code.UnboundedElement;
import mx.kenzie.foundation.assembler.vector.U2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public class CodeBuilder extends AttributableBuilder implements AttributeBuilder {

    protected final ClassFileBuilder.Storage storage;
    protected final MethodBuilder method;
    protected final PoolReference attributeName;
    private CodeVector vector;
    private int maxStack;
    private int maxLocals;
    private int stackCounter;

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

    public CodeBuilder maxLocals(int maxLocals) {
        this.maxLocals = maxLocals;
        return this;
    }

    public CodeBuilder maxStack(int maxStack) {
        this.maxStack = maxStack;
        return this;
    }

    /**
     * Tells the code block we're using a variable in this slot.
     * Used by parameters/store+load instructions to keep track of the register size.
     * This should be slot+1 for wide types.
     *
     * @param slot the upper register slot
     */
    public void notifyMaxLocalIndex(int slot) {
        if (maxLocals < slot + 1) maxLocals = slot + 1;
    }

    /**
     * Notifies the stack counter about any changes.
     *
     * @param increment The amount to increment (or reduce) by
     */
    public void notifyStack(int increment) {
        this.stackCounter += increment;
        if (stackCounter > maxStack) maxStack = stackCounter;
    }

    public CodeBuilder write(@NotNull UnboundedElement element) {
        final CodeElement bound = element.bound(storage);
        this.vector.append(bound);
        bound.notify(this);
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
        return this;
    }

    public CodeBuilder insertBefore(@NotNull CodeElement target, @NotNull UnboundedElement element) {
        final CodeElement bound = element.bound(storage);
        this.vector.insertBefore(target, bound);
        bound.notify(this);
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
        for (AttributeBuilder attribute : attributes) attribute.finalise();
    }

    @Override
    public ClassFileBuilder.Storage helper() {
        return storage;
    }

}
