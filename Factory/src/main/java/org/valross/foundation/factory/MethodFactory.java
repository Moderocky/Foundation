package org.valross.foundation.factory;

import org.valross.foundation.assembler.tool.Access;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.MethodBuilder;
import org.valross.foundation.detail.Erasure;
import org.valross.foundation.detail.Signature;
import org.valross.foundation.detail.Type;
import org.valross.foundation.factory.statement.Line;

import java.lang.invoke.TypeDescriptor;

public class MethodFactory extends ModifiableFactory<MethodBuilder> implements MemberFactory, Erasure {

    protected final ClassFactory factory;
    protected CodeBuilder code;

    protected MethodFactory(MethodBuilder builder, ClassFactory factory) {
        super(builder);
        this.factory = factory;
    }

    protected CodeBuilder code() {
        if (code == null) code = builder.code();
        return code;
    }

    @Override
    public MethodFactory name(String name) {
        this.builder.named(name);
        return this;
    }

    public MethodFactory returns(TypeDescriptor type) {
        this.builder.returns(Type.fromDescriptor(type));
        return this;
    }

    public MethodFactory parameters(TypeDescriptor... parameters) {
        this.builder.parameters(Type.array(parameters));
        return this;
    }

    @Override
    public MethodFactory signature(Signature signature) {
        this.builder.signature(signature);
        return this;
    }

    public MethodFactory modifiers(Access.Method... modifiers) {
        this.builder.setModifiers(modifiers);
        return this;
    }

    public MethodFactory line(Line line) {
        line.addTo(this.code());
        return this;
    }

    @Override
    public ClassFactory source() {
        return factory;
    }

    @Override
    public Type returnType() {
        return builder.returnType();
    }

    @Override
    public String name() {
        return builder.name();
    }

    @Override
    public Type[] parameters() {
        return builder.parameters();
    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public boolean isMethod() {
        return true;
    }

}
