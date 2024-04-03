package org.valross.foundation.assembler.constant;

import org.junit.Test;

public class Utf8InfoTest {

    @Test
    public void unpack0() {
        assert this.test("hello there");
        assert this.test("hello there!");
        assert this.test("general kenobi");
        assert this.test("hello 1023948 test");
        assert this.test("hello 1023948 test |!@£$^& beans");
        assert this.test("hello 1023948 ∑´®†¥˙¨∆^˚ø");
        assert this.test("¡€#¢∞§¶•ªº¡⁄c");
    }

    private boolean test(String value) {
        final Utf8Info info = Utf8Info.of(value);
        return info.unpack0().equals(value);
    }

}