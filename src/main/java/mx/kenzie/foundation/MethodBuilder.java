package mx.kenzie.foundation;

import mx.kenzie.foundation.error.PropertyCalculationError;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.RETURN;

public class MethodBuilder implements SubBuilder {
    
    protected final ClassBuilder builder;
    protected CodeWriter writer = new CodeWriter();
    protected String name;
    protected Type returnType;
    protected List<Type> parameters = new ArrayList<>();
    protected List<Type> thrown = new ArrayList<>();
    protected List<AnnotationBuilder<MethodBuilder>> annotations = new ArrayList<>();
    protected int modifiers;
    
    public MethodBuilder(ClassBuilder builder, String name) {
        this.builder = builder;
        this.name = name;
        this.returnType = new Type(void.class);
        this.modifiers = Modifier.PUBLIC;
    }
    
    static void addValue(AnnotationVisitor visitor, String name, Object value) {
        if (value instanceof Type type) {
            visitor.visit(name, org.objectweb.asm.Type.getType(type.descriptor()));
        } else visitor.visit(name, value);
    }
    
    static void visitAnnotation(AnnotationVisitor visitor, AnnotationBuilder<?> annotation) {
        for (Map.Entry<String, Object> entry : annotation.values.entrySet()) {
            if (entry.getValue() instanceof Enum e) {
                visitor.visitEnum(entry.getKey(), e.getDeclaringClass().descriptorString(), e.name());
            } else if (entry.getValue() instanceof Object[] array) {
                final AnnotationVisitor inner = visitor.visitArray(entry.getKey());
                for (Object o : array) {
                    addValue(inner, null, o);
                }
                inner.visitEnd();
            } else if (entry.getValue() instanceof AnnotationBuilder note) {
                final AnnotationVisitor inner = visitor.visitAnnotation(entry.getKey(), note.type.descriptorString());
                visitAnnotation(inner, note);
                inner.visitEnd();
            } else addValue(visitor, entry.getKey(), entry.getValue());
        }
        visitor.visitEnd();
    }
    
    public AnnotationBuilder<MethodBuilder> addAnnotation(Type type) {
        final AnnotationBuilder<MethodBuilder> builder = new AnnotationBuilder<>(this, type);
        annotations.add(builder);
        return builder;
    }
    
    public AnnotationBuilder<MethodBuilder> addAnnotation(Class<?> type) {
        final AnnotationBuilder<MethodBuilder> builder = new AnnotationBuilder<>(this, new Type(type));
        annotations.add(builder);
        return builder;
    }
    //endregion
    
    //region Type
    public MethodBuilder setReturnType(Class<?> type) {
        this.returnType = new Type(type);
        return this;
    }
    
    public MethodBuilder setReturnType(Type type) {
        this.returnType = type;
        return this;
    }
    //endregion
    
    //region Parameters
    public MethodBuilder addParameter(Class<?>... classes) {
        for (Class<?> aClass : classes) {
            parameters.add(new Type(aClass));
        }
        return this;
    }
    
    public MethodBuilder addParameter(Type... types) {
        this.parameters.addAll(Arrays.asList(types));
        return this;
    }
    //endregion
    
    //region Thrown Exceptions
    public MethodBuilder addThrown(Class<?>... classes) {
        for (Class<?> aClass : classes) {
            thrown.add(new Type(aClass));
        }
        return this;
    }
    
    public MethodBuilder addThrown(Type... types) {
        this.thrown.addAll(Arrays.asList(types));
        return this;
    }
    
    //region Code
    public MethodBuilder writeCode(WriteInstruction... instructions) {
        this.writer.addInstruction(instructions);
        return this;
    }
    //endregion
    
    public MethodErasure getErasure() {
        return new MethodErasure(returnType, name, parameters.toArray(new Type[0]));
    }
    //endregion
    
    //region Inherited
    @Override
    public ClassBuilder finish() {
        return builder;
    }
    
    //region Modifiers
    @Override
    public MethodBuilder setModifiers(int modifiers) {
        this.modifiers = modifiers;
        return this;
    }
    //endregion
    
    @Override
    public MethodBuilder addModifiers(int... modifiers) {
        for (int modifier : modifiers) {
            this.modifiers = this.modifiers | modifier;
        }
        return this;
    }
    
    @Override
    public MethodBuilder removeModifiers(int... modifiers) {
        for (int modifier : modifiers) {
            this.modifiers = this.modifiers & ~modifier;
        }
        return this;
    }
    
    void compile(ClassWriter writer) {
        final StringBuilder builder = new StringBuilder().append("(");
        for (Type parameter : parameters) {
            builder.append(parameter.descriptor());
        }
        builder.append(")").append(returnType.descriptor());
        final String[] exceptions;
        if (thrown.isEmpty()) exceptions = null;
        else {
            final List<String> strings = new ArrayList<>();
            for (final Type type : thrown) {
                strings.add(type.internalName());
            }
            exceptions = strings.toArray(new String[0]);
        }
        final MethodVisitor methodVisitor;
        methodVisitor = writer.visitMethod(modifiers, name, builder.toString(), null, exceptions);
        annotations:
        {
            for (AnnotationBuilder<MethodBuilder> annotation : annotations) {
                final AnnotationVisitor visitor = methodVisitor.visitAnnotation(annotation.type.descriptorString(), annotation.visible);
                visitAnnotation(visitor, annotation);
            }
        }
        write_code:
        {
            if (Modifier.isAbstract(modifiers)) break write_code;
            methodVisitor.visitCode();
            if (this.writer.isEmpty()) {
                methodVisitor.visitInsn(RETURN);
            } else {
                this.writer.write(methodVisitor);
            }
            try {
                methodVisitor.visitMaxs(0, 0);
            } catch (NegativeArraySizeException ex) {
                throw new PropertyCalculationError("Potential stack underflow detected while calculating frames.", ex);
            }
        }
        methodVisitor.visitEnd();
    }
}
