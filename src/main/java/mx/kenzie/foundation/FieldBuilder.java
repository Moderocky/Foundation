package mx.kenzie.foundation;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;

import java.lang.reflect.Modifier;

public class FieldBuilder implements SubBuilder {
    
    protected final ClassBuilder builder;
    protected String name;
    protected Type type;
    protected int modifiers;
    
    public FieldBuilder(ClassBuilder builder, String name) {
        this.builder = builder;
        this.name = name;
        this.modifiers = Modifier.PUBLIC;
    }
    
    //region Type
    public FieldBuilder setType(Class<?> type) {
        this.type = new Type(type);
        return this;
    }
    
    public FieldBuilder setType(Type type) {
        this.type = type;
        return this;
    }
    //endregion
    
    //region Modifiers
    @Override
    public FieldBuilder setModifiers(int modifiers) {
        this.modifiers = modifiers;
        return this;
    }
    
    @Override
    public FieldBuilder addModifiers(int... modifiers) {
        for (int modifier : modifiers) {
            this.modifiers = this.modifiers | modifier;
        }
        return this;
    }
    
    @Override
    public FieldBuilder removeModifiers(int... modifiers) {
        for (int modifier : modifiers) {
            this.modifiers = this.modifiers & ~modifier;
        }
        return this;
    }
    //endregion
    
    //region Inherited
    @Override
    public ClassBuilder finish() {
        return builder;
    }
    //endregion
    
    void compile(ClassWriter writer) {
        final FieldVisitor fieldVisitor;
        fieldVisitor = writer.visitField(modifiers, name, type.descriptor(), null, null);
        fieldVisitor.visitEnd();
    }
}
