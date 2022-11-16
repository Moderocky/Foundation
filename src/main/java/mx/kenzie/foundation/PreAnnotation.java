package mx.kenzie.foundation;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.invoke.TypeDescriptor;
import java.util.HashMap;
import java.util.Map;

public class PreAnnotation {
    protected final Map<String, Object> values = new HashMap<>();
    protected Type type;
    protected boolean visible;
    
    public <Klass extends java.lang.reflect.Type & TypeDescriptor>
    PreAnnotation(Klass type) {
        this.type = Type.of(type);
        this.visible = true;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public <Klass extends java.lang.reflect.Type & TypeDescriptor>
    void setType(Klass type) {
        this.type = Type.of(type);
    }
    
    public Map<String, Object> getValues() {
        return values;
    }
    
    public void addValue(String key, Object value) {
        this.values.put(key, value);
    }
    
    public <Klass extends java.lang.reflect.Type & TypeDescriptor>
    void addValueAnnotation(String name, Klass type) {
        final PreAnnotation annotation = new PreAnnotation(type);
        this.values.put(name, annotation);
    }
    
    protected void write(ClassWriter writer) {
        final AnnotationVisitor visitor = writer.visitAnnotation(type.descriptorString(), visible);
        this.write(visitor);
    }
    
    protected void write(MethodVisitor method) {
        final AnnotationVisitor visitor = method.visitAnnotation(type.descriptorString(), visible);
        this.write(visitor);
    }
    
    protected void write(FieldVisitor field) {
        final AnnotationVisitor visitor = field.visitAnnotation(type.descriptorString(), visible);
        this.write(visitor);
    }
    
    protected void write(AnnotationVisitor visitor) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            final Type from = Type.of(value.getClass());
            if (value instanceof Enum<?> num) visitor.visitEnum(key, from.descriptorString(), num.name());
            else if (value instanceof Object[] array) {
                final AnnotationVisitor inner = visitor.visitArray(key);
                for (Object object : array) this.addValue(inner, null, object);
                inner.visitEnd();
            } else if (value instanceof PreAnnotation annotation) {
                final AnnotationVisitor inner = visitor.visitAnnotation(key, annotation.type.descriptorString());
                annotation.write(inner);
            } else this.addValue(visitor, key, value);
        }
        visitor.visitEnd();
    }
    
    private void addValue(AnnotationVisitor visitor, String name, Object value) {
        if (value instanceof TypeDescriptor descriptor)
            visitor.visit(name, org.objectweb.asm.Type.getType(descriptor.descriptorString()));
        else visitor.visit(name, value);
    }
    
}
