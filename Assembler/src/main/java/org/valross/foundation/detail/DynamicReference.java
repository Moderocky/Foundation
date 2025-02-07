package org.valross.foundation.detail;

import org.jetbrains.annotations.NotNull;
import org.valross.constantine.Array;
import org.valross.constantine.Constant;
import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.constant.MethodTypeReference;
import org.valross.foundation.assembler.error.ConstantDeconstructionException;

import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import java.util.Optional;

public record DynamicReference(Kind type, Signature signature, Member.Invocation invocation, Object... arguments)
    implements RecordConstant {

    public DynamicReference(Kind type, Signature signature, Member.Invocation invocation, Constable... arguments) {
        this(type, signature, invocation, (Object[]) arguments);
    }

    public DynamicReference(Kind type, Signature signature, Member.Invocation invocation, ConstantDesc... arguments) {
        this(type, signature, invocation, (Object[]) arguments);
    }

    public static DynamicReference of(Array array) {
        final Optional<? extends ConstantDesc> described = array.describeConstable();
        if (described.isEmpty())
            throw new ConstantDeconstructionException("Constant array " + array + " has no description.");
        final DynamicConstantDesc<?> description = ((DynamicConstantDesc<?>) described.get());
        final Signature signature = new Signature(description.constantName(), Type.of(description.constantType()));
        final Constable[] entries = array.serial();
        return getDynamicReference(description, signature, (Object[]) entries);
    }

    public static DynamicReference of(Constant constant) {
        if (constant instanceof Array array) return of(array);
        final Optional<? extends ConstantDesc> described = constant.describeConstable();
        if (described.isEmpty())
            throw new ConstantDeconstructionException("Constant " + constant + " has no description.");
        final DynamicConstantDesc<?> description = ((DynamicConstantDesc<?>) described.get());
        final Signature signature = new Signature(description.constantName(), Type.of(description.constantType()));
        final Constable[] arguments, extended;
        try {
            arguments = constant.serial();
            extended = new Constable[arguments.length + 1];
            System.arraycopy(arguments, 0, extended, 1, arguments.length);
            extended[0] = MethodType.methodType(void.class, constant.canonicalParameters());
        } catch (Error error) {
            throw error;
        } catch (Throwable ex) {
            throw new ConstantDeconstructionException("Unable to fetch constant arguments", ex);
        }
        return getDynamicReference(description, signature, (Object[]) extended);
    }

    public static DynamicReference of(DynamicConstantDesc<?> description) {
        final Signature signature = new Signature(description.constantName(), Type.of(description.constantType()));
        return getDynamicReference(description, signature);
    }

    @NotNull
    private static DynamicReference getDynamicReference(DynamicConstantDesc<?> description, Signature signature,
                                                        Object... arguments) {
        final DirectMethodHandleDesc bootstrap = description.bootstrapMethod();
        final Member member = new Member(Type.of(bootstrap.owner()), bootstrap.methodName(),
                                         (TypeDescriptor) bootstrap::lookupDescriptor);
        final Member.Invocation invocation = member.dynamicInvocation(MethodTypeReference.INVOKE_STATIC);
        return new DynamicReference(Kind.CONSTANT, signature, invocation, arguments);
    }

    @NotNull
    private static DynamicReference getDynamicReference(DynamicConstantDesc<?> description, Signature signature) {
        return getDynamicReference(description, signature, (Object[]) description.bootstrapArgs());
    }

    public enum Kind {
        CONSTANT, INVOCATION
    }

}
