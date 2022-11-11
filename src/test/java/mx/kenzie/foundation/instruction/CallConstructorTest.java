package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.Loader;
import mx.kenzie.foundation.PreMethod;

import java.io.PrintStream;
import java.lang.reflect.Method;

import static mx.kenzie.foundation.Modifier.PUBLIC;
import static mx.kenzie.foundation.Modifier.STATIC;
import static mx.kenzie.foundation.Type.OBJECT;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class CallConstructorTest extends FoundationTest {
    
    public void test() throws Throwable {
        final PreMethod main = new PreMethod(PUBLIC, STATIC, OBJECT, "main");
        main.line(STORE_VAR.object(0, NEW.of(thing).make()));
        final CallMethod.Stub method = METHOD.of(thing, Object.class, "test", String.class);
        main.line(RETURN.object(method.get(LOAD_VAR.object(0), CONSTANT.of("hello"))));
        final PreMethod constructor = PreMethod.constructor(PUBLIC);
        constructor.line(SUPER.of(Object.class).call(LOAD_VAR.self()));
        constructor.line(RETURN.none());
        final PreMethod test = new PreMethod(PUBLIC, OBJECT, "test", String.class);
        test.line(RETURN.object(FIELD.of(System.class, "out", PrintStream.class).get()));
        thing.add(main);
        thing.add(constructor);
        thing.add(test);
        final Class<?> loaded = thing.load(Loader.DEFAULT);
        final Method entry = loaded.getDeclaredMethod("main");
        assert entry.invoke(null) == System.out;
    }
    
}
