package org.valross.foundation.factory;

import org.valross.foundation.assembler.tool.FieldBuilder;
import org.valross.foundation.detail.Erasure;
import org.valross.foundation.detail.Type;

public class FieldFactory extends Factory<FieldBuilder> implements Erasure {

    protected FieldFactory(FieldBuilder builder) {
        super(builder);
    }

    @Override
    public Type returnType() {
        return builder.;
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public Type[] parameters() {
        return new Type[0];
    }

}
