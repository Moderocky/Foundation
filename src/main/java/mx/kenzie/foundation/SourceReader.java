package mx.kenzie.foundation;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SourceReader {
    
    public static List<WriteInstruction> getSource(final Method method) {
        return getSource(method, null);
    }
    
    public static List<WriteInstruction> getSource(final Method method, final RewriteController controller) {
        final ClassReader reader = new ClassReader(getSource(method.getDeclaringClass()));
        final List<WriteInstruction> instructions = new ArrayList<>();
        if (controller == null)
            reader.accept(new MethodFinder(instructions, method.getName(), new Type(method.getReturnType()), Type.of(method.getParameterTypes()), null), ClassReader.SKIP_DEBUG);
        else {
            reader.accept(new MethodFinder(instructions, method.getName(), new Type(method.getReturnType()), Type.of(method.getParameterTypes()), controller), ClassReader.SKIP_DEBUG + (controller.isInline() ? ClassReader.SKIP_FRAMES : 0));
            if (controller.isInline()) instructions.add(controller.end());
        }
        return instructions;
    }
    
    public static byte[] getSource(final Class<?> cls) {
        try (final InputStream stream = cls.getClassLoader().getResourceAsStream(new Type(cls).internalName() + ".class")) {
            assert stream != null;
            return stream.readAllBytes();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static byte[] getSource(final Type type) {
        try (final InputStream stream = getSource0(type)) {
            assert stream != null;
            return stream.readAllBytes();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static InputStream getSource0(final Type type) {
        try {
            final InputStream stream = Class.forName(type.getTypeName()).getClassLoader().getResourceAsStream(type.internalName() + ".class");
            if (stream == null) throw new NullPointerException();
            return stream;
        } catch (Throwable ex) {
            return ClassLoader.getSystemResourceAsStream(type.internalName() + ".class");
        }
    }
    
    public static class MethodFinder extends ClassVisitor {
        final String signature;
        final String name;
        final List<WriteInstruction> instructions;
        final RewriteController controller;
        
        public MethodFinder(final List<WriteInstruction> instructions, final String name, Type returnType, Type[] parameters, RewriteController controller) {
            super(Opcodes.ASM9);
            this.controller = controller;
            this.instructions = instructions;
            final StringBuilder builder = new StringBuilder();
            builder.append("(");
            for (Type type : parameters) {
                builder.append(type.descriptorString());
            }
            builder.append(")").append(returnType.descriptor());
            this.name = name;
            signature = builder.toString();
        }
        
        private boolean inline() {
            return controller != null && controller.isInline();
        }
        
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals(this.name) && this.signature.equals(desc)) {
                return new CloneMethodVisitor(instructions, Opcodes.ASM9, super.visitMethod(access, name, desc, signature, exceptions), controller);
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
    
}
