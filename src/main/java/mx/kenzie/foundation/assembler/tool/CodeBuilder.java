package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.assembler.U2;
import mx.kenzie.foundation.assembler.attribute.AttributeInfo;
import mx.kenzie.foundation.assembler.attribute.Code;
import mx.kenzie.foundation.assembler.code.CodeElement;
import mx.kenzie.foundation.assembler.code.CodeVector;
import mx.kenzie.foundation.assembler.code.UnboundedElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public class CodeBuilder extends AttributableBuilder implements AttributeBuilder {

    protected final ClassFileBuilder.Storage storage;
    protected final PoolReference attributeName;
    private CodeVector vector;
    private int maxStack;
    private int maxLocals;
    private int stackCounter;

    public CodeBuilder(ClassFileBuilder.Storage storage) {
        this.storage = storage;
        this.attributeName = storage.constant(UTF8, "Code");
    }

    protected CodeBuilder writing(CodeVector vector) {
        this.vector = vector;
        return this;
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
        if (maxLocals < slot) maxLocals = slot;
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

    @Contract(pure = true)
    public CodeBuilder write() {
        return this;
    }

    @Contract(pure = true)
    public ClassFileBuilder exit() {
        return storage.source();
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
