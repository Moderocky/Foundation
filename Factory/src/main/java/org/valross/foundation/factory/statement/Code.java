package org.valross.foundation.factory.statement;

import org.valross.foundation.assembler.code.OpCode;
import org.valross.foundation.assembler.tool.CodePoint;

import java.lang.constant.Constable;
import java.lang.invoke.TypeDescriptor;

import static org.valross.foundation.assembler.code.OpCode.*;

/**
 * The set of code instructions.
 * Some instructions are {@link org.valross.foundation.factory.statement.Line}s, others are
 * {@link org.valross.foundation.factory.statement.Phrase}s.
 * A {@code Line} represents a complete statement of java code (e.g. {@code return 10;})
 * whereas a {@code Phrase} represents a value-providing part of that line (e.g. {@code 10}).
 */
public interface Code {

    @SuppressWarnings({"RedundantCast"})
    static Phrase<?> get(int slot, TypeDescriptor type) {
        return switch (type.descriptorString()) {
            case "I", "S", "C", "B", "Z" -> (IntPrimitivePhrase) ILOAD.var(slot)::addTo;
            case "F" -> (FloatPrimitivePhrase) FLOAD.var(slot)::addTo;
            case "J" -> (LongPrimitivePhrase) LLOAD.var(slot)::addTo;
            case "D" -> (DoublePrimitivePhrase) DLOAD.var(slot)::addTo;
            default -> ALOAD.var(slot)::addTo;
        };
    }

    @SuppressWarnings("unchecked")
    static <Value> Phrase<Value> get(int slot, Class<? super Value> type) {
        return (Phrase<Value>) get(slot, (TypeDescriptor) type);
    }

    static <Value extends Constable> Phrase<Value> literal(Value value) {
        return LDC.value(value)::addTo;
    }

    static Line set(int slot, Phrase<?> value) {
        if (value instanceof PrimitivePhrase<?> phrase) return switch (phrase.descriptorString()) {
            case "I", "Z", "C", "S", "B" -> OpCode.ISTORE.var(slot)::addTo;
            case "F" -> OpCode.FSTORE.var(slot)::addTo;
            case "J" -> OpCode.LSTORE.var(slot)::addTo;
            case "D" -> OpCode.DSTORE.var(slot)::addTo;
            default -> OpCode.ASTORE.var(slot)::addTo;
        };
        return OpCode.ASTORE.var(slot)::addTo;
    }

    static Line return$() {
        return OpCode.RETURN::addTo;
    }

    static Line return$(Phrase<?> value) {
        return of(value, switch (value.descriptorString()) {
            case "I", "Z", "C", "S", "B" -> IRETURN;
            case "F" -> FRETURN;
            case "J" -> LRETURN;
            case "D" -> DRETURN;
            default -> ARETURN;
        });
    }

    private static Line of(CodePoint... elements) {
        return builder -> {
            for (CodePoint element : elements) element.addTo(builder);
        };
    }

    private static ReturnLine return0(CodePoint value, CodePoint instruction) {
        return builder -> {
            value.addTo(builder);
            instruction.addTo(builder);
        };
    }

    interface ReturnLine extends Line {

    }

}
