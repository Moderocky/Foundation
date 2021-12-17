package mx.kenzie.foundation;

import mx.kenzie.foundation.language.PostCompileClass;
import mx.kenzie.foundation.opcodes.JavaVersion;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.invoke.TypeDescriptor;
import java.util.*;

import static mx.kenzie.foundation.MethodBuilder.visitAnnotation;
import static org.objectweb.asm.Opcodes.ASM9;

public class ClassBuilder {
    
    static final RuntimeClassLoader DEFAULT_LOADER = new RuntimeClassLoader();
    protected JavaVersion version;
    protected Type type;
    protected Type superclass = new Type(Object.class);
    protected final Set<Type> interfaces = new HashSet<>();
    protected int modifiers = 0;
    protected final List<FieldBuilder> fields = new ArrayList<>();
    protected final List<MethodBuilder> methods = new ArrayList<>();
    protected int computation = ClassWriter.COMPUTE_FRAMES;
    protected final List<ClassBuilder> suppressed = new ArrayList<>();
    protected RuntimeClassLoader loader = DEFAULT_LOADER;
    protected List<AnnotationBuilder<ClassBuilder>> annotations = new ArrayList<>();
    protected final Map<Type, Integer> inners = new HashMap<>();
    
    //region Create
    public ClassBuilder(PostCompileClass post) {
        this.version = JavaVersion.JAVA_16;
        this.type = Type.of(post.internalName());
    }
    
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
    
    public List<MethodBuilder> getMethods() {
        return methods;
    }
    
    public List<FieldBuilder> getFields() {
        return fields;
    }
    
    public MethodBuilder getMatching(MethodErasure erasure) {
        for (MethodBuilder method : methods) {
            if (method.getErasure().matches(erasure)) return method;
        }
        return null;
    }
    
    public MethodBuilder addMatching(MethodErasure erasure) {
        return addMethod(erasure.name()).addParameter(erasure.parameterTypes()).setReturnType(erasure.returnType());
    }
    
    public Type getType() {
        return type;
    }
    
    public boolean hasMatching(MethodErasure erasure) {
        for (MethodBuilder method : methods) {
            if (method.getErasure().matches(erasure)) return true;
        }
        return false;
    }
    
    public AnnotationBuilder<ClassBuilder> addAnnotation(Type type) {
        final AnnotationBuilder<ClassBuilder> builder = new AnnotationBuilder<>(this, type);
        annotations.add(builder);
        return builder;
    }
    
    public AnnotationBuilder<ClassBuilder> addAnnotation(Class<?> type) {
        final AnnotationBuilder<ClassBuilder> builder = new AnnotationBuilder<>(this, new Type(type));
        annotations.add(builder);
        return builder;
    }
    
    public JavaVersion getVersion() {
        return version;
    }
    
    public List<ClassBuilder> getSuppressed() {
        return new ArrayList<>(suppressed);
    }
    
    public ClassBuilder suppress(final ClassBuilder builder) {
        this.suppressed.add(builder);
        return this;
    }
    
    public String getName() {
        return type.dotPath();
    }
    
    public String getInternalName() {
        return type.internalName();
    }
    
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
    
    public ClassBuilder addInnerClass(Type type, int modifiers) {
        final String internal = type.internalName();
        final int x = internal.lastIndexOf('$');
        if (x < 0) throw new IllegalArgumentException("No nesting mark in '" + internal + "'");
        this.inners.put(type, modifiers);
        return this;
    }
    
    public byte[] compile() {
        final List<String> inheritance = new ArrayList<>();
        for (Type type : interfaces) {
            inheritance.add(type.internalName());
        }
        final ClassWriter writer = new ClassWriter(ASM9 + computation);
        writer.visit(version.version, modifiers, type.internalName(), null, superclass.internalName(), inheritance.toArray(new String[0]));
        annotations:
        {
            for (AnnotationBuilder<ClassBuilder> annotation : annotations) {
                final AnnotationVisitor visitor = writer.visitAnnotation(annotation.type.descriptorString(), annotation.visible);
                visitAnnotation(visitor, annotation);
            }
        }
        inners:
        {
            for (Map.Entry<Type, Integer> entry : inners.entrySet()) {
                final String internal = entry.getKey().internalName();
                final int x = internal.lastIndexOf('$');
                final String first = internal.substring(0, x);
                final String last = internal.substring(x + 1);
                writer.visitInnerClass(internal, first, last, entry.getValue());
            }
        }
        for (FieldBuilder field : fields) {
            field.compile(writer);
        }
        for (MethodBuilder method : methods) {
            method.compile(writer);
        }
        return writer.toByteArray();
    }
    
    public Class<?> compileAndLoad() {
        for (ClassBuilder builder : getSuppressed()) {
            builder.compileAndLoad();
        }
        return loadClass(type.getTypeName(), compile());
    }
    
    //region Class Loader
    public Class<?> loadClass(final String name, final byte[] bytes) {
        try {
            return Class.forName(name); // Can't duplicate-load
        } catch (Throwable ex) {
            return loader.loadClass(name, bytes);
        }
    }
    //endregion
    
}
