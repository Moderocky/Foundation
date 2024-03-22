package mx.kenzie.foundation;

import mx.kenzie.foundation.instruction.AccessField;
import org.junit.Test;

import static mx.kenzie.foundation.Modifier.*;
import static mx.kenzie.foundation.Type.BOOLEAN;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class PreFieldTest extends FoundationTest {

    @Test
    public void testSetType() {
        final PreField field = new PreField(Object.class, "blob");
        assert field.type == Type.OBJECT : field.type;
        field.setType(boolean.class);
        assert field.type == Type.BOOLEAN;
    }

    @Test
    public void testSetValue() {
        final PreField field = new PreField(PUBLIC, STATIC, FINAL, int.class, "testSetValue");
        field.setValue(10);
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testSetValue");
        method.line(STORE_VAR.intValue(0, FIELD.of(thing, "testSetValue", int.class).get()));
        method.line(RETURN.intValue(EQUALS.ints(LOAD_VAR.intValue(0), PUSH.byteValue(10))));
        this.thing.add(field);
        this.thing.add(method);
    }

    @Test
    public void testAddModifiers() {
        final PreField field = new PreField(PUBLIC, STATIC, int.class, "blob");
        assert !field.hasModifier(FINAL);
        field.addModifiers(FINAL);
        assert field.hasModifier(FINAL);
    }

    @Test
    public void testRemoveModifiers() {
        final PreField field = new PreField(PUBLIC, STATIC, FINAL, int.class, "blob");
        assert field.hasModifier(FINAL);
        field.removeModifiers(FINAL);
        assert !field.hasModifier(FINAL);
    }

    @Test
    public void testHasModifier() {
        final PreField field = new PreField(PUBLIC, STATIC, int.class, "blob");
        assert !field.hasModifier(FINAL);
        assert field.hasModifier(PUBLIC);
        assert field.hasModifier(STATIC);
        field.addModifiers(FINAL);
        field.removeModifiers(PUBLIC);
        assert field.hasModifier(FINAL);
        assert field.hasModifier(STATIC);
        assert !field.hasModifier(PUBLIC);
    }

    @Test
    public void testModifierCode() {
        final PreField field = new PreField(PUBLIC, STATIC, FINAL, int.class, "blob");
        assert java.lang.reflect.Modifier.isPublic(field.modifierCode());
        assert java.lang.reflect.Modifier.isStatic(field.modifierCode());
        assert java.lang.reflect.Modifier.isFinal(field.modifierCode());
        assert !java.lang.reflect.Modifier.isPrivate(field.modifierCode());
    }

    @Test
    public void testBuild() {
        final PreField field = new PreField(PUBLIC, STATIC, int.class, "testBuild");
        final AccessField.Stub stub = FIELD.of(thing, "testBuild", int.class);
        final PreMethod method = new PreMethod(PUBLIC, STATIC, BOOLEAN, "testBuild");
        method.line(stub.set(PUSH.byteValue(6)));
        method.line(STORE_VAR.intValue(0, stub.get()));
        method.line(RETURN.intValue(EQUALS.ints(LOAD_VAR.intValue(0), PUSH.byteValue(6))));
        this.thing.add(field);
        this.thing.add(method);
    }

}
