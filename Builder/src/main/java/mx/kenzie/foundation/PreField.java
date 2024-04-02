package mx.kenzie.foundation;

import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.detail.Modifier;
import mx.kenzie.foundation.detail.Type;
import mx.kenzie.foundation.instruction.AccessField;

import java.lang.constant.Constable;
import java.lang.invoke.TypeDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreField extends BuildElement implements AccessField.Stub {

    protected transient PreClass owner;
    protected Type type;
    protected String name;
    protected Set<Modifier> modifiers;
    protected Constable value;

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> PreField(Modifier access, Modifier state,
                                                                            Modifier change, Klass type, String name) {
        this(type, name);
        this.modifiers.add(access);
        this.modifiers.add(state);
        this.modifiers.add(change);
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> PreField(Klass type, String name) {
        this.name = name;
        this.type = Type.of(type);
        this.modifiers = new HashSet<>();
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> PreField(Modifier access, Modifier state,
                                                                            Klass type, String name) {
        this(type, name);
        this.modifiers.add(access);
        this.modifiers.add(state);
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> PreField(Modifier access, Klass type, String name) {
        this(type, name);
        this.modifiers.add(access);
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> void setType(Klass type) {
        this.type = Type.of(type);
    }

    public void setValue(Constable constant) {
        this.value = constant;
    }

    public void addModifiers(Modifier... modifiers) {
        this.modifiers.addAll(Arrays.asList(modifiers));
    }

    public void removeModifiers(Modifier... modifiers) {
        for (Modifier modifier : modifiers) this.modifiers.remove(modifier);
    }

    public boolean hasModifier(Modifier modifier) {
        return modifiers.contains(modifier);
    }

    @Override
    protected int modifierCode() {
        int modifiers = 0;
        for (Modifier modifier : this.modifiers) modifiers |= modifier.code;
        return modifiers;
    }

    @Override
    protected void build(ClassFileBuilder builder) {
        builder.field().setModifiers(this::modifierCode).named(name).ofType(type).constantValue(value);
//        for (PreAnnotation annotation : annotations) annotation.write(visitor);
    }

    @Override
    public Type owner() {
        return owner.type();
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }

}
