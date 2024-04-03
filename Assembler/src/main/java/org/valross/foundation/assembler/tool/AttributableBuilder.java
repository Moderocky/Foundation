package org.valross.foundation.assembler.tool;

import org.valross.foundation.assembler.attribute.AttributeInfo;
import org.valross.foundation.assembler.attribute.Deprecated;
import org.valross.foundation.assembler.attribute.Synthetic;
import org.valross.foundation.assembler.vector.U2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AttributableBuilder implements Builder {

    protected List<AttributeBuilder> attributes;

    public AttributableBuilder() {
        this.attributes = new ArrayList<>();
    }

    public AttributableBuilder synthetic() {
        return this.makeAttribute(Synthetic::new);
    }

    public AttributableBuilder deprecated() {
        return this.makeAttribute(Deprecated::new);
    }

    protected AttributableBuilder makeAttribute(Function<ClassFileBuilder.Storage, ? extends AttributeBuilder> attribute) {
        this.attributes.add(attribute.apply(this.helper()));
        return this;
    }

    protected AttributableBuilder attribute(AttributeBuilder attribute) {
        this.attributes.add(attribute);
        return this;
    }

    protected U2 attributesCount() {
        return U2.valueOf(attributes.size());
    }

    protected AttributeInfo[] attributes() {
        return this.attributes(new AttributeInfo[0]);
    }

    @SuppressWarnings("unchecked")
    protected <Type extends AttributeInfo> Type[] attributes(Type... array) {
        final AttributeInfo[] infos = (AttributeInfo[]) Array.newInstance(array.getClass().componentType(),
                                                                          attributes.size());
        for (int i = 0; i < infos.length; i++) infos[i] = attributes.get(i).build();
        return (Type[]) infos;
    }

    public abstract ClassFileBuilder.Storage helper();

}
