package mx.kenzie.foundation;

import mx.kenzie.foundation.instruction.Instruction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.invoke.TypeDescriptor;
import java.util.*;

public class PreMethod extends BuildElement implements CodeBody {
    protected transient PreClass owner;
    protected int stack, locals;
    protected List<Type> parameters;
    protected Type returnType;
    protected List<Instruction> instructions = new LinkedList<>();
    protected String name;
    protected Set<Modifier> modifiers;
    
    public PreMethod(String name) {
        this(Type.VOID, name);
    }
    
    @SafeVarargs
    public <Klass extends java.lang.reflect.Type & TypeDescriptor> PreMethod(Type returnType, String name, Klass... parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = new LinkedList<>(List.of(Type.array(parameters)));
        this.modifiers = new HashSet<>();
    }
    
    @SafeVarargs
    public <Klass extends java.lang.reflect.Type & TypeDescriptor> PreMethod(String name, Klass... parameters) {
        this(Type.VOID, name, parameters);
    }
    
    @SafeVarargs
    public <Klass extends java.lang.reflect.Type & TypeDescriptor> PreMethod(Modifier modifier, Type returnType, String name, Klass... parameters) {
        this(returnType, name, parameters);
        this.modifiers = new HashSet<>(List.of(modifier));
    }
    
    @SafeVarargs
    public <Klass extends java.lang.reflect.Type & TypeDescriptor> PreMethod(Modifier access, Modifier state, Type returnType, String name, Klass... parameters) {
        this(returnType, name, parameters);
        this.modifiers = new HashSet<>(List.of(access, state));
    }
    
    @SafeVarargs
    public static <Klass extends java.lang.reflect.Type & TypeDescriptor>
    PreMethod constructor(Klass... parameters) {
        return new PreMethod(Type.VOID, "<init>", parameters);
    }
    
    @SafeVarargs
    public static <Klass extends java.lang.reflect.Type & TypeDescriptor>
    PreMethod constructor(Modifier modifier, Klass... parameters) {
        return new PreMethod(modifier, Type.VOID, "<init>", parameters);
    }
    
    public <Klass extends java.lang.reflect.Type & TypeDescriptor> void setReturnType(Klass type) {
        this.returnType = Type.of(type);
    }
    
    public void addModifiers(Modifier... modifiers) {
        this.modifiers.addAll(Arrays.asList(modifiers));
    }
    
    public void removeModifiers(Modifier... modifiers) {
        for (Modifier modifier : modifiers) this.modifiers.remove(modifier);
    }
    
    public boolean hasModifier(Modifier modifier) {
        return modifiers.contains(modifier);
    }
    
    public void addParameters(Type... parameters) {
        this.parameters.addAll(Arrays.asList(parameters));
    }
    
    public void removeParameters(Type... parameters) {
        for (Type parameter : parameters) this.parameters.remove(parameter);
    }
    
    public void removeParameter(int index) {
        this.parameters.remove(index);
    }
    
    public Type[] getParameters() {
        return parameters.toArray(new Type[0]);
    }
    
    @Override
    public void line(Instruction.Base instruction) {
        this.instructions.add(instruction);
    }
    
    @Override
    public Instruction[] lines() {
        return instructions.toArray(new Instruction[0]);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }    protected String makeDescriptor() {
        final StringBuilder builder = new StringBuilder("(");
        for (Type parameter : parameters) builder.append(parameter.descriptorString());
        builder.append(')').append(returnType.descriptorString());
        return builder.toString();
    }
    

    
    @Override
    protected int modifierCode() {
        int modifiers = 0;
        for (Modifier modifier : this.modifiers) modifiers |= modifier.code;
        return modifiers;
    }
    
    @Override
    protected void build(ClassWriter writer) {
        final MethodVisitor visitor = writer.visitMethod(this.modifierCode(), name, this.makeDescriptor(), null, null);
        for (Instruction instruction : instructions) instruction.write(visitor);
        visitor.visitMaxs(stack, locals);
        visitor.visitEnd();
    }
    
    
}
