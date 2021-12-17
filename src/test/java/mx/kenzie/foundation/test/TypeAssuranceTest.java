package mx.kenzie.foundation.test;

import mx.kenzie.foundation.Type;
import org.junit.Test;

public class TypeAssuranceTest {
    
    @Test
    public void equality() {
        assert Object.class.descriptorString().equals(new Type(Object.class).descriptorString());
        assert org.objectweb.asm.Type.getInternalName(String.class).equals(new Type(String.class).internalName());
        assert int.class.descriptorString().equals(new Type(int.class).descriptorString());
        assert int[].class.descriptorString().equals(new Type(int[].class).descriptorString());
    }
    
    @Test
    public void array() {
        System.out.println(new Type(int[].class).dotPath());
        System.out.println(new Type(int[].class).internalName());
        System.out.println(new Type(int[].class).descriptor());
    }
    
}
