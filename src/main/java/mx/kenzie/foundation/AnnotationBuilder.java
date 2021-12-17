package mx.kenzie.foundation;

import java.util.HashMap;
import java.util.Map;

public class AnnotationBuilder<T> {
    
    final T builder;
    final Type type;
    final Map<String, Object> values = new HashMap<>();
    boolean visible;
    
    AnnotationBuilder(T builder, Type type) {
        this.builder = builder;
        this.type = type;
    }
    
    public AnnotationBuilder<T> setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }
    
    public AnnotationBuilder<T> addValue(String name, Object value) {
        this.values.put(name, value);
        return this;
    }
    
    public AnnotationBuilder<AnnotationBuilder<T>> addValueAnnotation(String name, Type type) {
        final AnnotationBuilder<AnnotationBuilder<T>> builder = new AnnotationBuilder<>(this, type);
        this.values.put(name, builder);
        return builder;
    }
    
    public AnnotationBuilder<AnnotationBuilder<T>> addValueAnnotation(String name, Class<?> type) {
        final AnnotationBuilder<AnnotationBuilder<T>> builder = new AnnotationBuilder<>(this, new Type(type));
        this.values.put(name, builder);
        return builder;
    }
    
    public T finish() {
        return builder;
    }
    
    
}
