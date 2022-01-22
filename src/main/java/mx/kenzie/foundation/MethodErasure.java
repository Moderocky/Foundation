package mx.kenzie.foundation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record MethodErasure(Type returnType, String name, Type... parameterTypes) {
    
    public MethodErasure(Method method) {
        this(method.getReturnType(), method.getName(), method.getParameterTypes());
    }
    
    public MethodErasure(Class<?> returnType, String name, Class<?>... parameterTypes) {
        this(new Type(returnType), name, Type.of(parameterTypes));
    }
    
    public static MethodErasure of(String name, String descriptor) {
        final org.objectweb.asm.Type type = org.objectweb.asm.Type.getMethodType(descriptor);
        final List<Type> types = new ArrayList<>();
        for (org.objectweb.asm.Type argument : type.getArgumentTypes()) {
            types.add(Type.of(argument.getInternalName()));
        }
        return new MethodErasure(Type.of(type.getReturnType().getInternalName()), name, types.toArray(new Type[0]));
    }
    
    public boolean matches(MethodErasure erasure) {
        return this.equals(erasure);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodErasure erasure)) return false;
        return Objects.equals(returnType, erasure.returnType) && Objects.equals(name, erasure.name) && Arrays.equals(parameterTypes, erasure.parameterTypes);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(returnType, name);
        result = 31 * result + Arrays.hashCode(parameterTypes);
        return result;
    }
    
    public boolean matches(Method method) {
        return this.equals(new MethodErasure(method));
    }
    
    public String getDescriptor() {
        return AccessUtility.getDescriptor(returnType, parameterTypes);
    }
}
