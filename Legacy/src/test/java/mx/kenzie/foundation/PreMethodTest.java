package mx.kenzie.foundation;

import org.valross.foundation.Loader;
import org.valross.foundation.detail.Type;
import mx.kenzie.foundation.instruction.CallMethod;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.valross.foundation.detail.Modifier.*;
import static org.valross.foundation.detail.Type.*;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class PreMethodTest extends FoundationTest {

    @Test
    public void testSetReturnType() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, STRING, "testSetReturnType");
        method.line(RETURN.none());
        thing.add(method);
        assert method.owner == thing;
        assert method.returnType.toClass() == String.class;
        method.setReturnType(void.class);
        assert method.returnType.toClass() == void.class;
    }

    @Test
    public void testLine() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, STRING, "testLine");
        method.line(STORE_VAR.object(0, CONSTANT.of("hello")));
        method.line(RETURN.object(LOAD_VAR.object(0)));
        thing.add(method);
        assert method.owner == thing;
        assert method.returnType.toClass() == String.class;
    }

    @Test
    public void testCallMethod() throws Throwable {
        final PreClass blob = new PreClass("org.example", "Thing2");
        final PreMethod getter = new PreMethod(PUBLIC, STATIC, STRING, "testCallMethodGet");
        getter.line(RETURN.object(CONSTANT.of("beans")));
        final PreMethod method = new PreMethod(PUBLIC, STATIC, STRING, "testCallMethod");
        blob.add(getter);
        blob.add(method);
        final CallMethod.Stub stub = METHOD.of(blob, STRING, "testCallMethodGet");
        method.line(STORE_VAR.object(0, stub.getStatic()));
        method.line(RETURN.object(LOAD_VAR.object(0)));
        assert method.owner == blob;
        assert method.returnType.toClass() == String.class;
        final Class<?> test = blob.load(Loader.DEFAULT);
        final Method found = test.getDeclaredMethod("testCallMethod");
        final String result = (String) found.invoke(null);
        assert result.equals("beans");
        this.dump(blob);
    }

    @Test
    public void testAddModifiers() {
        final PreMethod method = new PreMethod(PUBLIC, STRING, "testAddModifiers");
        assert !java.lang.reflect.Modifier.isStatic(method.modifierCode());
        method.addModifiers(STATIC);
        assert java.lang.reflect.Modifier.isPublic(method.modifierCode());
        assert java.lang.reflect.Modifier.isStatic(method.modifierCode());
    }

    @Test
    public void testRemoveModifiers() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, STRING, "testRemoveModifiers");
        assert java.lang.reflect.Modifier.isPublic(method.modifierCode());
        assert java.lang.reflect.Modifier.isStatic(method.modifierCode());
        method.removeModifiers(STATIC);
        assert !java.lang.reflect.Modifier.isStatic(method.modifierCode());
        method.addModifiers(ABSTRACT);
        assert java.lang.reflect.Modifier.isPublic(method.modifierCode());
        assert java.lang.reflect.Modifier.isAbstract(method.modifierCode());
    }

    @Test
    public void testHasModifier() {
        final PreMethod method = new PreMethod(PRIVATE, STATIC, STRING, "testHasModifier");
        assert !java.lang.reflect.Modifier.isPublic(method.modifierCode());
        assert java.lang.reflect.Modifier.isPrivate(method.modifierCode());
        assert java.lang.reflect.Modifier.isStatic(method.modifierCode());
        assert method.hasModifier(PRIVATE);
        assert method.hasModifier(STATIC);
        assert !method.hasModifier(PUBLIC);
        assert !method.hasModifier(ABSTRACT);
    }

    @Test
    public void testAddParameters() {
        final PreMethod method = new PreMethod("testAddParameters");
        assert method.getParameters().length == 0;
        method.addParameters(STRING, BOOLEAN);
        assert method.getParameters().length == 2;
        assert method.getParameters()[1] == BOOLEAN;
    }

    @Test
    public void testRemoveParameters() {
        final PreMethod method = new PreMethod("testRemoveParameters", INT, LONG, INT);
        assert method.getParameters().length == 3;
        method.removeParameters(INT);
        final Type[] types = method.getParameters();
        assert types.length == 2;
        assert types[0] == LONG;
        assert types[1] == INT;
    }

    @Test
    public void testRemoveParameter() {
        final PreMethod method = new PreMethod("testRemoveParameter", INT, LONG, INT);
        assert method.getParameters().length == 3;
        method.removeParameter(1);
        final Type[] types = method.getParameters();
        assert types.length == 2;
        assert types[0] == INT;
        assert types[1] == INT;
    }

    @Test
    public void testGetParameters() {
        final PreMethod method = new PreMethod("testGetParameters", INT, LONG, INT);
        final Type[] types = method.getParameters();
        assert types.length == 3;
        assert types[0] == INT;
        assert types[1] == LONG;
        assert types[2] == INT;
    }

}
