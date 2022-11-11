package mx.kenzie.foundation;

import org.objectweb.asm.ClassWriter;

abstract class BuildElement {
    
    protected abstract int modifierCode();
    
    protected abstract void build(ClassWriter writer);
    
}
