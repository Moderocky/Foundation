package mx.kenzie.foundation;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static mx.kenzie.foundation.MethodBuilder.visitAnnotation;

public class FieldBuilder implements SubBuilder {
    
    protected final ClassBuilder builder;
    protected String name;
    protected Type type;
    protected int modifiers;
    protected List<AnnotationBuilder<FieldBuilder>> annotations = new ArrayList<>();
    
    public FieldBuilder(ClassBuilder builder, String name) {
        this.builder = builder;
        this.name = name;
        this.modifiers = Modifier.PUBLIC;
    }
    
    public AnnotationBuilder<FieldBuilder> addAnnotation(Type type) {
        final AnnotationBuilder<FieldBuilder> builder = new AnnotationBuilder<>(this, type);
        annotations.add(builder);
        return builder;
    }
    
    public AnnotationBuilder<FieldBuilder> addAnnotation(Class<?> type) {
        final AnnotationBuilder<FieldBuilder> builder = new AnnotationBuilder<>(this, new Type(type));
        annotations.add(builder);
        return builder;
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
        annotations: {
            for (AnnotationBuilder<FieldBuilder> annotation : annotations) {
                final AnnotationVisitor visitor = fieldVisitor.visitAnnotation(annotation.type.descriptorString(), annotation.visible);
                visitAnnotation(visitor, annotation);
            }
        }
        fieldVisitor.visitEnd();
    }
}
