package mx.kenzie.foundation;

import static mx.kenzie.foundation.Modifier.PUBLIC;
import static mx.kenzie.foundation.Modifier.STATIC;
import static mx.kenzie.foundation.Type.*;
import static mx.kenzie.foundation.instruction.Instruction.RETURN;

public class PreClassTest extends FoundationTest {
    
    public void testAdd() {
        final PreMethod method = new PreMethod(PUBLIC, STATIC, VOID, "testAdd", BOOLEAN, INT);
        method.line(RETURN.none());
        thing.add(method);
        assert method.owner == thing;
        assert method.returnType.toClass() == void.class;
        assert method.name.equals("testAdd");
        assert method.parameters.contains(BOOLEAN);
        assert method.parameters.contains(INT);
        assert thing.verify();
    }
    
    public void testMultipleMethods() {
        final PreClass thing = new PreClass("org.example", "MultipleMethods");
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, VOID, "testA", BOOLEAN, INT);
            method.line(RETURN.none());
            thing.add(method);
        }
        {
            final PreMethod method = new PreMethod(PUBLIC, STATIC, VOID, "testB", BOOLEAN, INT);
            method.line(RETURN.none());
            thing.add(method);
        }
        assert thing.verify();
        this.dump(thing);
    }
    
}
