package mx.kenzie.foundation;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreClass extends BuildElement implements TypeDescriptor, java.lang.reflect.Type {
    
    protected final Set<Type> interfaces = new HashSet<>();
    protected final Set<Modifier> modifiers = new HashSet<>();
    protected final Set<PreMethod> methods = new HashSet<>();
    protected final Set<PreField> fields = new HashSet<>();
    protected int version;
    protected String path, name;
    protected Type type, parent;
    
    public PreClass(String path, String name) {
        this(Opcodes.V17, path, name);
    }
    
    public PreClass(int version, String path, String name) {
        this.version = version;
        this.parent = Type.OBJECT;
        this.modifiers.add(Modifier.PUBLIC);
        this.path = path;
        this.name = name;
        final String internal = path.replace('.', '/') + '/' + name;
        this.type = new Type(path + '.' + name, 'L' + internal + ';', internal);
    }
    
    public PreField add(PreField field) {
        this.fields.add(field);
        field.owner = this;
        return field;
    }
    
    public PreMethod add(PreMethod method) {
        this.methods.add(method);
        method.owner = this;
        return method;
    }
    
    public void setAbstract(boolean isAbstract) {
        if (isAbstract) modifiers.add(Modifier.ABSTRACT);
        else {
            modifiers.remove(Modifier.INTERFACE);
            modifiers.remove(Modifier.ABSTRACT);
        }
    }
    
    public void setInterface(boolean anInterface) {
        if (anInterface) {
            modifiers.add(Modifier.ABSTRACT);
            modifiers.add(Modifier.INTERFACE);
        } else modifiers.remove(Modifier.INTERFACE);
    }
    
    public void setParent(Type parent) {
        this.parent = parent;
    }
    
    public void addInterfaces(Type... interfaces) {
        this.interfaces.addAll(Arrays.asList(interfaces));
    }
    
    public void remove(PreMethod method) {
        this.methods.remove(method);
        if (method.owner == this) method.owner = null;
    }
    
    public boolean verify() {
        final Loader loader = new SimpleClassLoader(ClassLoader.getSystemClassLoader());
        final Class<?> loaded = this.load(loader);
        final Type type = Type.of(loaded);
        assert this.type.equals(type);
        for (PreMethod method : methods) {
            final Class<?>[] parameters = Type.classArray(method.parameters.toArray(new Type[0]));
            Method found;
            try {
                found = loaded.getDeclaredMethod(method.name, parameters);
            } catch (NoSuchMethodException ex) {
                return false;
            }
            assert found.getReturnType() == method.returnType.toClass();
            assert found.getModifiers() == method.modifierCode();
        }
        return true;
    }
    
    public Class<?> load(Loader loader) {
        final byte[] bytes = this.bytecode();
        return loader.loadClass(type.getTypeName(), bytes);
    }
    
    public byte[] bytecode() {
        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        this.build(writer);
        return writer.toByteArray();
    }
    
    @Override
    protected int modifierCode() {
        int modifiers = 0;
        for (Modifier modifier : this.modifiers) modifiers |= modifier.code;
        return modifiers;
    }
    
    @Override
    protected void build(ClassWriter writer) {
        writer.visit(version, this.modifierCode(), type.internalName(), null, parent.internalName(), null);
        // todo annotations
        for (PreField field : fields) field.build(writer);
        for (PreMethod method : methods) method.build(writer);
    }
    
    @Override
    public String descriptorString() {
        return type.descriptorString();
    }
    
    @Override
    public String getTypeName() {
        return type.getTypeName();
    }
}
