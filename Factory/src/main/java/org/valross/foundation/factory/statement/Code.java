package org.valross.foundation.factory.statement;

import org.valross.foundation.assembler.tool.CodePoint;

import java.lang.constant.Constable;
import java.lang.invoke.TypeDescriptor;

import static org.valross.foundation.assembler.code.OpCode.*;

/**
 * The set of code instructions.
 * Some instructions are {@link Line}s, others are
 * {@link Phrase}s.
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <Value extends Constable, P extends Phrase<? super Value>> P literal(Value value) {
        return switch (value) {
            case Integer _, Short _, Character _ -> (P) (IntPrimitivePhrase) LDC.value(value)::addTo;
            case Double _ -> (P) (DoublePrimitivePhrase) LDC.value(value)::addTo;
            case Float _ -> (P) (FloatPrimitivePhrase) LDC.value(value)::addTo;
            case Long _ -> (P) (LongPrimitivePhrase) LDC.value(value)::addTo;
            case Number _ ->  (P) (NumericPrimitivePhrase) LDC.value(value)::addTo;
            case Boolean _ ->  (P) (PrimitivePhrase) LDC.value(value)::addTo;
            case null -> (P) (Phrase) ACONST_NULL::addTo;
            default -> (P) (Phrase) LDC.value(value)::addTo;
        };
    }

    static Line set(int slot, Phrase<?> value) {
        CodePoint store = value instanceof PrimitivePhrase<?> phrase ? switch (phrase.descriptorString()) {
            case "I", "Z", "C", "S", "B" -> ISTORE.var(slot);
            case "F" -> FSTORE.var(slot);
            case "J" -> LSTORE.var(slot);
            case "D" -> DSTORE.var(slot);
            default -> ASTORE.var(slot);
        } : ASTORE.var(slot);
        return builder -> {
            value.addTo(builder);
            store.addTo(builder);
        };
    }

    static Line return$() {
        return RETURN::addTo;
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
