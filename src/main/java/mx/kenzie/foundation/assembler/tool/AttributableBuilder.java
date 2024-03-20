package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.assembler.attribute.AttributeInfo;

import java.util.List;

public abstract class AttributableBuilder {

    protected List<AttributeInfo> attributes;

    public AttributableBuilder synthetic() {
        return this;
    }

    public AttributableBuilder deprecated() {
        return this;
    }

    protected AttributableBuilder attribute(AttributeInfo attribute) {
        this.attributes.add(attribute);
        return this;
    }

    public abstract ClassFileBuilder.Storage helper();

}
