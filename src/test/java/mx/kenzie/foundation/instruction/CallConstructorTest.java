package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.FoundationTest;
import mx.kenzie.foundation.PreMethod;
import org.junit.Test;

import java.io.PrintStream;

import static mx.kenzie.foundation.Modifier.PUBLIC;
import static mx.kenzie.foundation.Modifier.STATIC;
import static mx.kenzie.foundation.Type.BOOLEAN;
import static mx.kenzie.foundation.Type.OBJECT;
import static mx.kenzie.foundation.instruction.Instruction.*;

public class CallConstructorTest extends FoundationTest {
    
    @Test
    public void test() {
        final PreMethod main = new PreMethod(PUBLIC, STATIC, BOOLEAN, "main");
        main.line(STORE_VAR.object(0, NEW.of(thing).make()));
        final CallMethod.Stub method = METHOD.of(thing, Object.class, "test", String.class);
        main.line(RETURN.intValue(EQUALS.objects(method.get(LOAD_VAR.object(0), CONSTANT.of("hello")), FIELD.of(System.class, "out", PrintStream.class)
            .get())));
        final PreMethod constructor = PreMethod.constructor(PUBLIC);
        constructor.line(SUPER.of(Object.class).call(LOAD_VAR.self()));
        constructor.line(RETURN.none());
        final PreMethod test = new PreMethod(PUBLIC, OBJECT, "test", String.class);
        test.line(RETURN.object(FIELD.of(System.class, "out", PrintStream.class).get()));
        thing.add(main);
        thing.add(constructor);
        thing.add(test);
    }
    
}
