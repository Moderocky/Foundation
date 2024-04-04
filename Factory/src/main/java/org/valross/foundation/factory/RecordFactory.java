package org.valross.foundation.factory;

import org.valross.foundation.assembler.attribute.Record;
import org.valross.foundation.assembler.code.UnboundedElement;
import org.valross.foundation.assembler.code.VariableCode;
import org.valross.foundation.assembler.tool.Access;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.detail.Erasure;
import org.valross.foundation.detail.Signature;

import java.lang.invoke.TypeDescriptor;
import java.util.ArrayList;
import java.util.List;

import static org.valross.foundation.assembler.code.OpCode.*;

public class RecordFactory extends ClassFactory {

    protected List<Signature> components;

    protected RecordFactory(ClassFileBuilder builder) {
        super(builder);
        super.extend(java.lang.Record.class);
        this.components = new ArrayList<>();
    }

    @Override
    public RecordFactory modifiers(Access.Type... modifiers) {
        return (RecordFactory) super.modifiers(modifiers);
    }

    @Override
    public RecordFactory implement(TypeDescriptor... types) {
        return (RecordFactory) super.implement(types);
    }

    @Override
    public RecordFactory extend(TypeDescriptor type) {
        throw new UnsupportedOperationException("Records may extend only Record.");
    }

    public RecordFactory components(Signature... components) {
        this.components.clear();
        for (Signature component : components) this.components.add(component.asFieldErasure());
        this.builder.removeAttribute(Record.class);
        this.builder.attribute(storage -> Record.of(storage, components));
        for (Signature component : components)
            this.assureField(component).assureMethod(component);
        return this;
    }

    public RecordFactory component(Signature component) {
        component = component.asFieldErasure();
        this.components.add(component);
        this.builder.removeAttribute(Record.class);
        this.builder.attribute(storage -> Record.of(storage, components.toArray(new Signature[0])));
        return this.assureField(component).assureMethod(component);
    }

    public RecordFactory component(String name, TypeDescriptor type) {
        return this.component(new Signature(name, type));
    }

    public RecordFactory constructor() {
        final CodeBuilder code = super.constructor(components).code();
        code.write(ALOAD_0, INVOKESPECIAL.constructor(java.lang.Record.class));
        int count = 0;
        for (Signature component : components) {
            final VariableCode load = switch (component.descriptorString()) {
                case "I", "S", "C", "B", "Z" -> ILOAD;
                case "F" -> FLOAD;
                case "J" -> LLOAD;
                case "D" -> DLOAD;
                default -> ALOAD;
            };
            code.write(load.var(++count), PUTFIELD.field(this, component));
        }
        code.write(RETURN);
        return this;
    }

    private RecordFactory assureField(Erasure component) {
        if (this.builder.helper().isFieldPresent(component)) return this;
        this.field(component.getSignature()).modifiers(Access.PRIVATE, Access.FINAL);
        return this;
    }

    private RecordFactory assureMethod(Erasure component) {
        if (this.builder.helper().isMethodPresent(component.asMethodErasure())) return this;
        final UnboundedElement exit = switch (component.descriptorString()) {
            case "I", "S", "C", "B", "Z" -> IRETURN;
            case "J" -> LRETURN;
            case "D" -> DRETURN;
            default -> ARETURN;
        };
        this.method(component.asMethodErasure().getSignature()).modifiers(Access.PUBLIC, Access.FINAL)
            .code().write(ALOAD_0, GETFIELD.field(this, component), exit);
        return this;
    }

}
