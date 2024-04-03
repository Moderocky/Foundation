package org.valross.foundation.assembler.tool;

import org.junit.Test;

import static org.valross.foundation.assembler.tool.Access.*;

public abstract class ModifiableBuilderTest extends AttributableBuilderTest {

    protected abstract ModifiableBuilder example();

    @Test
    public void setModifiers() {
        final ModifiableBuilder builder = this.example();
        assert builder.modifiers().value() == 0;
        builder.setModifiers(PUBLIC);
        assert Access.is(builder.access_flags, PUBLIC);
        assert !Access.is(builder.access_flags, PRIVATE);
        assert !Access.is(builder.access_flags, STATIC);
        assert !Access.is(builder.access_flags, SYNTHETIC);
        assert builder.modifiers().intValue() == PUBLIC.intValue();
        builder.setModifiers(PRIVATE, SYNTHETIC);
        assert Access.is(builder.access_flags, PRIVATE);
        assert Access.is(builder.access_flags, SYNTHETIC);
        assert !Access.is(builder.access_flags, PUBLIC);
    }

    @Test
    public void addModifiers() {
        final ModifiableBuilder builder = this.example();
        assert builder.modifiers().value() == 0;
        builder.addModifiers(PUBLIC);
        assert Access.is(builder.access_flags, PUBLIC);
        assert !Access.is(builder.access_flags, PRIVATE);
        assert !Access.is(builder.access_flags, STATIC);
        assert !Access.is(builder.access_flags, SYNTHETIC);
        assert builder.modifiers().intValue() == PUBLIC.intValue();
        builder.addModifiers(SYNTHETIC);
        assert Access.is(builder.access_flags, PUBLIC);
        assert Access.is(builder.access_flags, SYNTHETIC);
        assert !Access.is(builder.access_flags, PRIVATE);
    }

    @Test
    public void modifiers() {
        final ModifiableBuilder builder = this.example();
        assert builder.modifiers().value() == 0;
        builder.setModifiers(PUBLIC);
        assert builder.modifiers() == builder.access_flags;
        assert builder.modifiers().intValue() > 0;
    }

    @Test
    public void hasModifier() {
        final ModifiableBuilder builder = this.example();
        assert builder.modifiers().value() == 0;
        builder.addModifiers(PUBLIC);
        assert Access.is(builder.access_flags, PUBLIC);
        assert builder.hasModifier(PUBLIC);
        assert !builder.hasModifier(PRIVATE);
        assert !builder.hasModifier(SYNTHETIC);
        builder.addModifiers(SYNTHETIC);
        assert builder.hasModifier(PUBLIC);
        assert builder.hasModifier(SYNTHETIC);
        assert !builder.hasModifier(STATIC);
    }

}