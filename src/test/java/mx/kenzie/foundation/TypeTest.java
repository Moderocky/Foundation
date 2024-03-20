package mx.kenzie.foundation;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TypeTest {

    @Test
    public void testToClass() {
        final Type type = Type.of(TypeTest.class);
        assert type.toClass() == TypeTest.class;
        final Type primitive = Type.of(int.class);
        assert primitive.toClass() == int.class;
        assert Type.VOID.toClass() == void.class;
    }

    @Test
    public void testOf() {
        final Type a = Type.of(TypeTest.class);
        final Type b = Type.of(TypeTest.class);
        final Type c = Type.of(void.class);
        assert c == Type.VOID;
        assert a == b;
    }

    @Test
    public void testArray() {
        final Type[] types = Type.array(int.class, void.class);
        assert types.length == 2;
        assert types[0].toClass() == int.class;
        assert types[1] == Type.VOID;
    }

    @Test
    public void testInternalName() {
        assert Type.of(String.class).internalName().equals("java/lang/String");
        assert Type.of(Object.class).internalName().equals("java/lang/Object");
        assert Type.of(TypeTest.class).internalName().equals("mx/kenzie/foundation/TypeTest");
    }

    @Test
    public void testIsPrimitive() {
        assert Type.of(int.class).isPrimitive();
        assert Type.VOID.isPrimitive();
        assert !Type.of(Object.class).isPrimitive();
    }

    @Test
    public void testGetTypeName() {
        assert Type.of(Object.class).getTypeName().equals(Object.class.getTypeName());
    }

    @Test
    public void testDescriptorString() {
        assert Type.of(Object.class).descriptorString().equals(org.objectweb.asm.Type.getDescriptor(Object.class));
        assert Type.of(int.class).descriptorString().equals(org.objectweb.asm.Type.getDescriptor(int.class));
        assert Type.VOID.descriptorString().equals(org.objectweb.asm.Type.getDescriptor(void.class));
    }

    @Test
    public void internalName() throws Throwable {
        final Method method = TypeTest.class.getDeclaredMethod("dummy");
        final java.lang.reflect.Type parameterised = method.getAnnotatedReturnType().getType();
        assert parameterised.getTypeName().equals("java.util.List<java.lang.String>");
        assert Type.internalName(parameterised).equals("java/util/List");
        final java.lang.reflect.Type primitive = int.class;
        assert primitive.getTypeName().equals("int") : primitive.getTypeName();
        assert Type.INT.internalName().equals(org.objectweb.asm.Type.getInternalName(int.class));
        assert Type.internalName(primitive).equals(org.objectweb.asm.Type.getInternalName(int.class));
    }

    @Test
    public void descriptorString() throws Throwable {
        final Method method = TypeTest.class.getDeclaredMethod("dummy");
        final java.lang.reflect.Type parameterised = method.getAnnotatedReturnType().getType();
        assert parameterised.getTypeName().equals("java.util.List<java.lang.String>");
        assert Type.descriptorString(parameterised).equals("Ljava/util/List;");
        final java.lang.reflect.Type primitive = int.class;
        assert primitive.getTypeName().equals("int") : primitive.getTypeName();
        assert Type.INT.descriptorString().equals(org.objectweb.asm.Type.getDescriptor(int.class));
        assert Type.descriptorString(primitive).equals(org.objectweb.asm.Type.getDescriptor(int.class));
    }

    private List<String> dummy() {
        return new ArrayList<>();
    }

    @Test
    public void fromDescriptor() {
        assert Type.fromDescriptor(Object.class).equals(Type.OBJECT);
        assert Type.fromDescriptor(int.class).equals(Type.INT);
        assert Type.fromDescriptor(String.class).equals(Type.STRING);
        assert Type.fromDescriptor(Object[].class).equals(Type.OBJECT.arrayType());
        assert Type.fromDescriptor(int[].class).equals(Type.INT.arrayType());
        assert Type.fromDescriptor(int[][].class).equals(Type.INT.arrayType().arrayType());
    }

    @Test
    public void parameters() {
        assert Type.parameters(Descriptor.of(void.class)).length == 0;
        assert Type.parameters(Descriptor.of(void.class, String.class)).length == 1;
        assert Type.parameters(Descriptor.of(void.class, int.class, String.class)).length == 2;
        assert Type.parameters(Descriptor.of(void.class, int.class, String.class))[0].equals(Type.INT);
        assert Type.parameters(Descriptor.of(void.class, int.class, String.class))[1].equals(Type.STRING);
        assert Type.parameters(Descriptor.of(Object.class, String.class))[0].equals(Type.STRING);
        assert Type.parameters(Descriptor.of(Object.class, String[].class))[0].equals(Type.STRING.arrayType());
        assert Type.parameters(Descriptor.of(Object.class, String[].class))[0].equals(Type.of(String[].class));
    }

}
