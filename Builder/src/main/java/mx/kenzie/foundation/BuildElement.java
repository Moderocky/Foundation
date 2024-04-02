package mx.kenzie.foundation;

import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.detail.Modifier;

import java.util.HashSet;
import java.util.Set;

abstract class BuildElement {

    protected final Set<PreAnnotation> annotations = new HashSet<>();

    public abstract void addModifiers(Modifier... modifiers);

    public abstract void removeModifiers(Modifier... modifiers);

    public abstract boolean hasModifier(Modifier modifier);

    protected abstract int modifierCode();

    protected abstract void build(ClassFileBuilder builder);

    public Set<PreAnnotation> getAnnotations() {
        return annotations;
    }

    public void addAnnotation(PreAnnotation annotation) {
        this.annotations.add(annotation);
    }

}
