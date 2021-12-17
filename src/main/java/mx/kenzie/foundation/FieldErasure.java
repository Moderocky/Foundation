package mx.kenzie.foundation;

import java.lang.reflect.Field;
import java.util.Objects;

public record FieldErasure(Type type, String name) {
    
    public FieldErasure(Class<?> returnType, String name) {
        this(new Type(returnType), name);
    }
    
    public FieldErasure(Field field) {
        this(field.getType(), field.getName());
    }
    
    public boolean matches(FieldErasure erasure) {
        return this.equals(erasure);
    }
    
    public boolean matches(Field field) {
        return this.equals(new FieldErasure(field));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldErasure erasure)) return false;
        return Objects.equals(type, erasure.type) && Objects.equals(name, erasure.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }
    
}
