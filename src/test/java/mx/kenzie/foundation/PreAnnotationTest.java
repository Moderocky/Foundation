package mx.kenzie.foundation;

import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;

import static mx.kenzie.foundation.instruction.Instruction.RETURN;

public class PreAnnotationTest extends FoundationTest {

    @Test
    public void testSetVisible() throws Throwable {
        final PreClass test = new PreClass("org.example", "testSetVisible");
        {
            final PreMethod method = new PreMethod(Modifier.STATIC, Type.VOID, "testSetVisible1");
            method.addAnnotation(new PreAnnotation(First.class));
            method.line(RETURN.none());
            test.add(method);
        }
        {
            final PreMethod method = new PreMethod(Modifier.STATIC, Type.VOID, "testSetVisible2");
            final PreAnnotation annotation = new PreAnnotation(First.class);
            annotation.visible = false;
            method.addAnnotation(annotation);
            method.line(RETURN.none());
            test.add(method);
        }
        final Class<?> loaded = test.load(Loader.DEFAULT);
        final Method yes = loaded.getDeclaredMethod("testSetVisible1");
        final Method no = loaded.getDeclaredMethod("testSetVisible2");
        assert yes.isAnnotationPresent(First.class);
        assert !no.isAnnotationPresent(First.class);
    }

    @Test
    public void testSetType() throws Throwable {
        final PreClass test = new PreClass("org.example", "testSetType");
        {
            final PreMethod method = new PreMethod(Modifier.STATIC, Type.VOID, "testSetType1");
            method.addAnnotation(new PreAnnotation(First.class));
            method.line(RETURN.none());
            test.add(method);
        }
        {
            final PreMethod method = new PreMethod(Modifier.STATIC, Type.VOID, "testSetType2");
            final PreAnnotation annotation = new PreAnnotation(First.class);
            annotation.setType(Second.class);
            method.addAnnotation(annotation);
            method.line(RETURN.none());
            test.add(method);
        }
        final Class<?> loaded = test.load(Loader.DEFAULT);
        final Method yes = loaded.getDeclaredMethod("testSetType1");
        final Method no = loaded.getDeclaredMethod("testSetType2");
        assert yes.isAnnotationPresent(First.class);
        assert !no.isAnnotationPresent(First.class);
        assert !yes.isAnnotationPresent(Second.class);
        assert no.isAnnotationPresent(Second.class);
        assert yes.getAnnotation(First.class).value().equals("hello");
    }

    @Test
    public void testGetValues() throws Throwable {
        final PreClass test = new PreClass("org.example", "testGetValues");
        final PreMethod method = new PreMethod(Modifier.STATIC, Type.VOID, "testGetValues");
        final PreAnnotation annotation = new PreAnnotation(First.class);
        annotation.addValue("value", "there");
        annotation.addValue("valueInt", 6);
        annotation.addValue("valueClass", String[].class);
        annotation.addValue("array", new String[] {"hello", "there"});
        method.addAnnotation(annotation);
        method.addAnnotation(new PreAnnotation(Second.class));
        method.line(RETURN.none());
        test.add(method);
        final Class<?> loaded = test.load(Loader.DEFAULT);
        final Method result = loaded.getDeclaredMethod("testGetValues");
        assert result.getAnnotation(Second.class).value().equals("bean");
        assert result.getAnnotation(Second.class).valueInt() == 0;
        assert result.getAnnotation(Second.class).valueClass() == void.class;
        assert result.getAnnotation(First.class).value().equals("there");
        assert result.getAnnotation(First.class).valueInt() == 6;
        assert result.getAnnotation(First.class).valueClass() == String[].class;
        final String[] array = result.getAnnotation(First.class).array();
        assert Arrays.equals(array, new String[] {"hello", "there"});
    }

    @Test
    public void testAddValueAnnotation() throws Throwable {
        final PreClass test = new PreClass("org.example", "testAddValueAnnotation");
        final PreAnnotation annotation = new PreAnnotation(Third.class);
        final PreAnnotation next = new PreAnnotation(Fourth.class);
        next.addValue("value", 2);
        annotation.addValue("blob", next);
        test.addAnnotation(annotation);
        final Class<?> loaded = test.load(Loader.DEFAULT);
        assert loaded.isAnnotationPresent(Third.class);
        final Third third = loaded.getAnnotation(Third.class);
        assert third.value().equals("test");
        assert third.blob() != null;
        assert third.blob().value() == 2;
    }

    @Test
    public void testWrite() {
        final PreAnnotation third = new PreAnnotation(Third.class);
        final PreAnnotation fourth = new PreAnnotation(Fourth.class);
        fourth.addValue("value", 2);
        third.addValue("blob", fourth);
        final PreMethod method = new PreMethod(Modifier.STATIC, Type.VOID, "testGetValues");
        final PreAnnotation annotation = new PreAnnotation(First.class);
        annotation.addValue("value", "there");
        annotation.addValue("valueInt", 6);
        annotation.addValue("valueClass", String[].class);
        annotation.addValue("array", new String[] {"hello", "there"});
        method.addAnnotation(annotation);
        method.addAnnotation(new PreAnnotation(Second.class));
        method.line(RETURN.none());
        this.thing.addAnnotation(third);
        this.thing.add(method);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
    public @interface First {

        String value() default "hello";

        Class<?> valueClass() default void.class;

        int valueInt() default 0;

        String[] array() default {};

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
    public @interface Second {

        String value() default "bean";

        int valueInt() default 0;

        Class<?> valueClass() default void.class;

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
    public @interface Third {

        String value() default "test";

        Fourth blob();

    }

    public @interface Fourth {

        int value() default 0;

    }

}
