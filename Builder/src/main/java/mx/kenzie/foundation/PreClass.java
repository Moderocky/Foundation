package mx.kenzie.foundation;

import mx.kenzie.foundation.detail.Type;
import mx.kenzie.foundation.detail.Modifier;
import mx.kenzie.foundation.detail.UnloadedClass;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Method;
import java.util.*;

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

    @Override
    public void addModifiers(Modifier... modifiers) {
        this.modifiers.addAll(Arrays.asList(modifiers));
    }

    @Override
    public void removeModifiers(Modifier... modifiers) {
        for (Modifier modifier : modifiers) this.modifiers.remove(modifier);
    }

    @Override
    public boolean hasModifier(Modifier modifier) {
        return modifiers.contains(modifier);
    }

    @Override
    protected int modifierCode() {
        int modifiers = 0;
        for (Modifier modifier : this.modifiers) modifiers |= modifier.code;
        return modifiers;
    }

    @Override
    protected void build(ClassWriter writer) {
        final List<String> interfaces = new ArrayList<>(this.interfaces.size());
        for (Type anInterface : this.interfaces) {
            interfaces.add(anInterface.internalName());
        }
        writer.visit(version, this.modifierCode(), type.internalName(), null, parent.internalName(),
            interfaces.toArray(new String[0]));
        for (PreAnnotation annotation : annotations) annotation.write(writer);
        for (PreField field : fields) field.build(writer);
        for (PreMethod method : methods) method.build(writer);
    }

    public void setAbstract(boolean isAbstract) {
        if (isAbstract) modifiers.add(Modifier.ABSTRACT);
        else {
            modifiers.remove(Modifier.INTERFACE);
            modifiers.remove(Modifier.ABSTRACT);
        }
    }

    public boolean isInterface() {
        return modifiers.contains(Modifier.INTERFACE);
    }

    public void setInterface(boolean anInterface) {
        if (anInterface) {
            modifiers.add(Modifier.ABSTRACT);
            modifiers.add(Modifier.INTERFACE);
        } else modifiers.remove(Modifier.INTERFACE);
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> void setParent(Klass parent) {
        this.parent = Type.of(parent);
    }

    @SafeVarargs
    public final <Klass extends java.lang.reflect.Type & TypeDescriptor> void addInterfaces(Klass... interfaces) {
        this.interfaces.addAll(Arrays.asList(Type.array(interfaces)));
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
            final Method found;
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

    public UnloadedClass compile() {
        return new UnloadedClass(type, this.bytecode());
    }

    @Override
    public String descriptorString() {
        return type.descriptorString();
    }

    @Override
    public String getTypeName() {
        return type.getTypeName();
    }

    public Type type() {
        return type;
    }

}
