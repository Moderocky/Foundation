package mx.kenzie.foundation;

import junit.framework.TestCase;

public class TypeTest extends TestCase {

    public void testToClass() {
        final Type type = Type.of(TypeTest.class);
        assert type.toClass() == TypeTest.class;
        final Type primitive = Type.of(int.class);
        assert primitive.toClass() == int.class;
        assert Type.VOID.toClass() == void.class;
    }

    public void testOf() {
        final Type a = Type.of(TypeTest.class);
        final Type b = Type.of(TypeTest.class);
        final Type c = Type.of(void.class);
        assert c == Type.VOID;
        assert a == b;
    }

    public void testArray() {
        final Type[] types = Type.array(int.class, void.class);
        assert types.length == 2;
        assert types[0].toClass() == int.class;
        assert types[1] == Type.VOID;
    }

    public void testInternalName() {
        assert Type.of(String.class).internalName().equals("java/lang/String");
        assert Type.of(Object.class).internalName().equals("java/lang/Object");
        assert Type.of(TypeTest.class).internalName().equals("mx/kenzie/foundation/TypeTest");
    }

    public void testIsPrimitive() {
        assert Type.of(int.class).isPrimitive();
        assert Type.VOID.isPrimitive();
        assert !Type.of(Object.class).isPrimitive();
    }

    public void testGetTypeName() {
        assert Type.of(Object.class).getTypeName().equals(Object.class.getTypeName());
    }

    public void testDescriptorString() {
        assert Type.of(Object.class).descriptorString().equals(org.objectweb.asm.Type.getDescriptor(Object.class));
        assert Type.of(int.class).descriptorString().equals(org.objectweb.asm.Type.getDescriptor(int.class));
        assert Type.VOID.descriptorString().equals(org.objectweb.asm.Type.getDescriptor(void.class));
    }
}
