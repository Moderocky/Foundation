package mx.kenzie.foundation;

import org.valross.foundation.detail.Type;
import mx.kenzie.foundation.instruction.AccessField;
import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.valross.foundation.detail.Modifier.*;
import static org.valross.foundation.detail.Type.BOOLEAN;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class PreFieldTest extends FoundationTest {

    /** Checks that the field type is being changed correctly from {@code OBJECT} to {@code BOOLEAN}.*/
    @Test
    public void testWhenSetTypeCalled_typeShouldBeUpdated() {
        final PreField field = new PreField(Object.class, "blob");
        assertEquals(Type.OBJECT, field.type);
        field.setType(boolean.class);
        assertEquals(Type.BOOLEAN, field.type);
    }

    /** Checks the setting of the field value and the creation of a method that verifies this value.*/
    @Test
    public void testWhenSetValueCalled_fieldValueShouldBeSetAndVerifiable() {
        final PreField field = new PreField(PUBLIC, STATIC, FINAL, int.class, "testSetValue");
        field.setValue(10);

        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testSetValue");
        method.line(STORE_VAR.intValue(0, FIELD.of(thing, "testSetValue", int.class).get()));
        method.line(RETURN.intValue(EQUALS.ints(LOAD_VAR.intValue(0), PUSH.byteValue(10))));

        thing.add(field);
        thing.add(method);
    }

    /** Checks the addition of the {@code FINAL} modifier to the field.*/
    @Test
    public void testWhenModifierAdded_fieldShouldHaveThatModifier() {
        final PreField field = new PreField(PUBLIC, STATIC, int.class, "blob");
        assertFalse(field.hasModifier(FINAL));
        field.addModifiers(FINAL);
        assertTrue(field.hasModifier(FINAL));
    }

    /** Checks for removing the {@code FINAL} modifier from the field.*/
    @Test
    public void testWhenModifierRemoved_fieldShouldNotHaveThatModifier() {
        final PreField field = new PreField(PUBLIC, STATIC, FINAL, int.class, "blob");
        assertTrue(field.hasModifier(FINAL));
        field.removeModifiers(FINAL);
        assertFalse(field.hasModifier(FINAL));
    }

    /** Checks the presence and absence of modifiers {@code (PUBLIC, STATIC, FINAL)} in the field.*/
    @Test
    public void testWhenCheckingModifiers_fieldShouldReflectCorrectModifiers() {
        final PreField field = new PreField(PUBLIC, STATIC, int.class, "blob");
        assertFalse(field.hasModifier(FINAL));
        assertTrue(field.hasModifier(PUBLIC));
        assertTrue(field.hasModifier(STATIC));

        field.addModifiers(FINAL);
        field.removeModifiers(PUBLIC);

        assertTrue(field.hasModifier(FINAL));
        assertTrue(field.hasModifier(STATIC));
        assertFalse(field.hasModifier(PUBLIC));
    }

    /** Verifies that the field modifier code {@code (PUBLIC, STATIC, FINAL)} is correctly reflected in the {@code modifierCode}.*/
    @Test
    public void testWhenModifierCodeChecked_shouldReflectCorrectModifiers() {
        final PreField field = new PreField(PUBLIC, STATIC, FINAL, int.class, "blob");
        assertTrue(Modifier.isPublic(field.modifierCode()));
        assertTrue(Modifier.isStatic(field.modifierCode()));
        assertTrue(Modifier.isFinal(field.modifierCode()));
        assertFalse(Modifier.isPrivate(field.modifierCode()));
    }

    /** Checks the creation of the field and the method that interacts with this field (sets the value and returns the result of the comparison).*/
    @Test
    public void testWhenFieldAndMethodBuilt_shouldInteractCorrectly() {
        final PreField field = new PreField(PUBLIC, STATIC, int.class, "testBuild");
        final AccessField.Stub stub = FIELD.of(thing, "testBuild", int.class);

        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testBuild");
        method.line(stub.set(PUSH.byteValue(6)));
        method.line(STORE_VAR.intValue(0, stub.get()));
        method.line(RETURN.intValue(EQUALS.ints(LOAD_VAR.intValue(0), PUSH.byteValue(6))));

        thing.add(field);
        thing.add(method);
    }
}