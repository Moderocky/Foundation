package mx.kenzie.foundation;

import org.objectweb.asm.ClassWriter;

import java.util.HashSet;
import java.util.Set;

abstract class BuildElement {
    protected final Set<PreAnnotation> annotations = new HashSet<>();
    
    public abstract void addModifiers(Modifier... modifiers);
    
    public abstract void removeModifiers(Modifier... modifiers);
    
    public abstract boolean hasModifier(Modifier modifier);
    
    protected abstract int modifierCode();
    
    protected abstract void build(ClassWriter writer);
    
    public Set<PreAnnotation> getAnnotations() {
        return annotations;
    }
    
    public void addAnnotation(PreAnnotation annotation) {
        this.annotations.add(annotation);
    }
    
}
