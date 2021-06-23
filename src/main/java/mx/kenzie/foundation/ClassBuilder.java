package mx.kenzie.foundation;

import mx.kenzie.foundation.opcodes.JavaVersion;
import org.objectweb.asm.ClassWriter;

import java.lang.invoke.TypeDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.objectweb.asm.Opcodes.ASM9;

public class ClassBuilder {
    
    protected RuntimeClassLoader loader = new RuntimeClassLoader();
    protected JavaVersion version;
    protected Type type;
    protected Type superclass = new Type(Object.class);
    protected final Set<Type> interfaces = new HashSet<>();
    protected int modifiers = 0;
    protected final List<FieldBuilder> fields = new ArrayList<>();
    protected final List<MethodBuilder> methods = new ArrayList<>();
    protected int computation = ClassWriter.COMPUTE_FRAMES;
    
    //region Create
    public ClassBuilder(Type type, JavaVersion version) {
        this.version = version;
        this.type = type;
    }
    
    public ClassBuilder(Type type) {
        this(type, JavaVersion.JAVA_16);
    }
    
    public ClassBuilder(String path) {
        this(new Type(path), JavaVersion.JAVA_16);
    }
    //endregion
    
    //region Modifiers
    public ClassBuilder setModifiers(int modifiers) {
        this.modifiers = modifiers;
        return this;
    }
    
    public ClassBuilder addModifiers(int... modifiers) {
        for (int modifier : modifiers) {
            this.modifiers = this.modifiers | modifier;
        }
        return this;
    }
    
    public ClassBuilder removeModifiers(int... modifiers) {
        for (int modifier : modifiers) {
            this.modifiers = this.modifiers & ~modifier;
        }
        return this;
    }
    
    public ClassBuilder setComputation(int computation) {
        this.computation = computation;
        return this;
    }
    //endregion
    
    //region Inheritance
    public <T extends java.lang.reflect.Type & TypeDescriptor>
    ClassBuilder setSuperclass(T type) {
        superclass = new Type(type);
        return this;
    }
    
    public ClassBuilder addInterfaces(Class<?>... types) {
        for (Class<?> t : types) {
            if (!t.isInterface()) continue;
            interfaces.add(new Type(t));
        }
        return this;
    }
    
    public ClassBuilder addInterfaces(Type... types) {
        for (Type t : types) {
            interfaces.add(new Type(t));
        }
        return this;
    }
    //endregion
    
    public FieldBuilder addField(String name) {
        final FieldBuilder builder = new FieldBuilder(this, name);
        fields.add(builder);
        return builder;
    }
    
    public MethodBuilder addMethod(String name) {
        final MethodBuilder builder = new MethodBuilder(this, name);
        methods.add(builder);
        return builder;
    }
    
    public ConstructorBuilder addConstructor() {
        final ConstructorBuilder builder = new ConstructorBuilder(this);
        methods.add(builder);
        return builder;
    }
    
    public byte[] compile() {
        final List<String> inheritance = new ArrayList<>();
        for (Type type : interfaces) {
            inheritance.add(type.internalName());
        }
        final ClassWriter writer = new ClassWriter(ASM9 + computation);
        writer.visit(version.version, modifiers, type.descriptor(), null, superclass.internalName(), inheritance.toArray(new String[0]));
        for (FieldBuilder field : fields) {
            field.compile(writer);
        }
        for (MethodBuilder method : methods) {
            method.compile(writer);
        }
        return writer.toByteArray();
    }
    
    public Class<?> compileAndLoad() {
        return loadClass(type.getTypeName(), compile());
    }
    
    //region Class Loader
    protected Class<?> loadClass(final String name, final byte[] bytes) {
        return loader.loadClass(name, bytes);
    }
    
    static class RuntimeClassLoader extends ClassLoader {
        public Class<?> loadClass(String name, byte[] bytecode) {
            return defineClass(name, bytecode, 0, bytecode.length);
        }
    }
    //endregion
    
}
