package org.valross.foundation.factory.statement;

import org.valross.foundation.assembler.tool.CodePoint;
import org.valross.foundation.detail.Type;

import java.lang.invoke.TypeDescriptor;

public interface Phrase<Value> extends CodePoint, TypeDescriptor {

    default boolean primitive() {
        return false;
    }

    default @Override
    String descriptorString() {
        return Type.OBJECT.descriptorString();
    }

}

interface PrimitivePhrase<Wrapper> extends Phrase<Wrapper>, TypeDescriptor {

}

interface NumericPrimitivePhrase<Wrapper extends Number> extends PrimitivePhrase<Wrapper> {

}

interface IntPrimitivePhrase extends NumericPrimitivePhrase<Integer> {

    default @Override
    String descriptorString() {
        return "I";
    }

}

interface FloatPrimitivePhrase extends NumericPrimitivePhrase<Float> {

    default @Override
    String descriptorString() {
        return "F";
    }

}

interface DoublePrimitivePhrase extends NumericPrimitivePhrase<Double> {

    default @Override
    String descriptorString() {
        return "D";
    }

}

interface LongPrimitivePhrase extends NumericPrimitivePhrase<Long> {

    default @Override
    String descriptorString() {
        return "J";
    }

}
