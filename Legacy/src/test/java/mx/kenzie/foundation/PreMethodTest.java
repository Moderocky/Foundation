package mx.kenzie.foundation;

import org.valross.foundation.Loader;
import org.valross.foundation.detail.Type;
import mx.kenzie.foundation.instruction.CallMethod;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.valross.foundation.detail.Modifier.*;
import static org.valross.foundation.detail.Type.*;
import static mx.kenzie.foundation.instruction.Instruction.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;


public class PreMethodTest extends FoundationTest {

    /**
     * Tests the ability to set and change the return type of a method.
     * Verifies that the return type is correctly updated.
     */
    @Test
    public void testSetAndChangeReturnType() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, STRING, "testSetReturnType");
        method.line(RETURN.none());
        thing.add(method);
        assertEquals(thing, method.owner);
        assertEquals(String.class, method.returnType.toClass());

        method.setReturnType(void.class);
        assertEquals(void.class, method.returnType.toClass());
    }

    /**
     * Tests adding lines of code to a method and verifies that the method's return type is correct.
     */
    @Test
    public void testAddCodeLinesToMethod() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, STRING, "testLine");
        method.line(STORE_VAR.object(0, CONSTANT.of("hello")));
        method.line(RETURN.object(LOAD_VAR.object(0)));
        thing.add(method);

        assertEquals(thing, method.owner);
        assertEquals(String.class, method.returnType.toClass());
    }

    /**
     * Tests calling another method within a method and verifies the result of the method call.
     * Also checks that the method is correctly added to the class and can be invoked.
     */
    @Test
    public void testCallAnotherMethodWithinMethod() throws Throwable {
        final PreClass blob = new PreClass("org.example", "Thing2");
        final PreMethod getter = new PreMethod(PUBLIC, STATIC, STRING, "testCallMethodGet");
        getter.line(RETURN.object(CONSTANT.of("beans")));
        final PreMethod method = new PreMethod(PUBLIC, STATIC, STRING, "testCallMethod");
        blob.add(getter);
        blob.add(method);
        final CallMethod.Stub stub = METHOD.of(blob, STRING, "testCallMethodGet");
        method.line(STORE_VAR.object(0, stub.getStatic()));
        method.line(RETURN.object(LOAD_VAR.object(0)));

        assertEquals(blob, method.owner);
        assertEquals(String.class, method.returnType.toClass());

        final Class<?> test = blob.load(Loader.DEFAULT);
        final Method found = test.getDeclaredMethod("testCallMethod");
        final String result = (String) found.invoke(null);
        assertEquals("beans", result);
        this.dump(blob);
    }

    /**
     * Tests adding modifiers to a method and verifies that the modifiers are correctly applied.
     */
    @Test
    public void testAddModifiersToMethod() {
        final PreMethod method = new PreMethod(PUBLIC, STRING, "testAddModifiers");
        assertFalse(java.lang.reflect.Modifier.isStatic(method.modifierCode()));
        method.addModifiers(STATIC);
        assertTrue(java.lang.reflect.Modifier.isPublic(method.modifierCode()));
        assertTrue(java.lang.reflect.Modifier.isStatic(method.modifierCode()));
    }

    /**
     * Tests removing modifiers from a method and verifies that the modifiers are correctly removed.
     * Also tests adding a new modifier and verifies its application.
     */
    @Test
    public void testRemoveAndAddModifiersToMethod() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, STRING, "testRemoveModifiers");
        assertTrue(java.lang.reflect.Modifier.isPublic(method.modifierCode()));
        assertTrue(java.lang.reflect.Modifier.isStatic(method.modifierCode()));
        method.removeModifiers(STATIC);
        assertFalse(java.lang.reflect.Modifier.isStatic(method.modifierCode()));

        method.addModifiers(ABSTRACT);
        assertTrue(java.lang.reflect.Modifier.isPublic(method.modifierCode()));
        assertTrue(java.lang.reflect.Modifier.isAbstract(method.modifierCode()));
    }

    /**
     * Tests checking if a method has specific modifiers and verifies the results.
     */
    @Test
    public void testCheckMethodModifiers() {
        final PreMethod method = new PreMethod(PRIVATE, STATIC, STRING, "testHasModifier");
        assertFalse(java.lang.reflect.Modifier.isPublic(method.modifierCode()));
        assertTrue(java.lang.reflect.Modifier.isPrivate(method.modifierCode()));
        assertTrue(java.lang.reflect.Modifier.isStatic(method.modifierCode()));
        assertTrue(method.hasModifier(PRIVATE));
        assertTrue(method.hasModifier(STATIC));
        assertFalse(method.hasModifier(PUBLIC));
        assertFalse(method.hasModifier(ABSTRACT));
    }

    /**
     * Tests adding parameters to a method and verifies that the parameters are correctly added.
     */
    @Test
    public void testAddParametersToMethod() {
        final PreMethod method = new PreMethod("testAddParameters");
        assertEquals(0, method.getParameters().length);

        method.addParameters(STRING, BOOLEAN);
        assertEquals(2, method.getParameters().length);
        assertEquals(BOOLEAN, method.getParameters()[1]);
    }

    /**
     * Tests removing specific parameters from a method and verifies that the parameters are correctly removed.
     */
    @Test
    public void testRemoveSpecificParametersFromMethod() {
        final PreMethod method = new PreMethod("testRemoveParameters", INT, LONG, INT);
        assertEquals(3, method.getParameters().length);

        method.removeParameters(INT);
        final Type[] types = method.getParameters();
        assertEquals(2, types.length);
        assertEquals(LONG, types[0]);
        assertEquals(INT, types[1]);
    }

    /**
     * Tests removing a parameter at a specific index from a method and verifies that the parameter is correctly removed.
     */
    @Test
    public void testRemoveParameterAtIndexFromMethod() {
        final PreMethod method = new PreMethod("testRemoveParameter", INT, LONG, INT);
        assertEquals(3, method.getParameters().length);

        method.removeParameter(1);
        final Type[] types = method.getParameters();
        assertEquals(2, types.length);
        assertEquals(INT, types[0]);
        assertEquals(INT, types[1]);
    }

    /**
     * Tests retrieving the parameters of a method and verifies that the parameters are correctly returned.
     */
    @Test
    public void testRetrieveMethodParameters() {
        final PreMethod method = new PreMethod("testGetParameters", INT, LONG, INT);
        final Type[] types = method.getParameters();
        assertEquals(3, types.length);
        assertEquals(INT, types[0]);
        assertEquals(LONG, types[1]);
        assertEquals(INT, types[2]);
    }
}
