package org.valross.foundation.factory.statement;

import org.junit.Test;
import org.valross.foundation.factory.ClassFactoryTest;

import static org.valross.foundation.factory.statement.Code.*;

public class CodeTest extends ClassFactoryTest {

    @Test
    public void getTest() {
        assert Code.get(0, int.class) instanceof PrimitivePhrase<Integer>;
        assert Code.get(0, int.class) instanceof IntPrimitivePhrase;
        assert Code.get(0, byte.class) instanceof PrimitivePhrase<Byte>;
        assert (Object) Code.get(0, byte.class) instanceof IntPrimitivePhrase;
        assert Code.get(0, float.class) instanceof PrimitivePhrase<Float>;
        assert Code.get(0, float.class) instanceof FloatPrimitivePhrase;
        assert Code.get(0, double.class) instanceof PrimitivePhrase<Double>;
        assert Code.get(0, double.class) instanceof DoublePrimitivePhrase;
        assert Code.get(0, long.class) instanceof PrimitivePhrase<Long>;
        assert Code.get(0, long.class) instanceof LongPrimitivePhrase;
    }

    @Test
    public void setTest() {
        this.test(this.methodFactory().returns(int.class)
            .line(set(0, literal(1)))
            .line(return$(get(0, int.class))), 1);
        this.test(this.methodFactory().returns(double.class)
            .line(set(0, literal(2.0)))
            .line(return$(get(0, double.class))), 2.0);
    }

    @Test
    public void returnTest() {
    }

}