package mx.kenzie.foundation;

import org.objectweb.asm.ClassWriter;

abstract class BuildElement {
    
    public abstract void addModifiers(Modifier... modifiers);
    
    public abstract void removeModifiers(Modifier... modifiers);
    
    public abstract boolean hasModifier(Modifier modifier);
    
    protected abstract int modifierCode();
    
    protected abstract void build(ClassWriter writer);
    
}
