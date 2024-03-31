package mx.kenzie.foundation.detail;

import mx.kenzie.foundation.assembler.constant.MethodTypeReference;
import org.intellij.lang.annotations.MagicConstant;
import org.valross.constantine.RecordConstant;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * A 'invocation' of a class, such as a method or a field.
 * This contains all the information needed to reference the invocation directly.
 * It is not specified what type of thing this is (e.g. a method or a field)
 * although that can be inferred from its signature where necessary.
 *
 * @param owner     The class the invocation comes from
 * @param signature The invocation's signature
 */
public record Member(Type owner, Signature signature) implements Erasure, Descriptor, RecordConstant {

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> Member(Klass owner, Erasure erasure) {
        this(Type.of(owner), erasure.getSignature());
    }

    @SafeVarargs
    public <Klass extends java.lang.reflect.Type & TypeDescriptor> Member(Klass owner, Klass returnType, String name,
                                                                          Klass... parameters) {
        this(Type.of(owner), new Signature(returnType, name, parameters));
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> Member(Klass owner, String name, Klass type) {
        this(Type.of(owner), new Signature(name, type));
    }

    public Member(Field field) {
        this(field.getDeclaringClass(), field.getName(), field.getType());
    }

    public Member(java.lang.reflect.Method method) {
        this(method.getDeclaringClass(), new Signature(method));
    }

    /**
     * Converts this to a dynamic method invocation stub (for the invoke dynamic instruction).
     * This attempts to guess whether an interface instruction is required.
     *
     * @param type The type of access operation we're going to perform, see {@link MethodTypeReference}
     */
    public Invocation dynamicInvocation(@MagicConstant(valuesFromClass = MethodTypeReference.class) int type) {
        return this.dynamicInvocation(type, owner().isKnownInterface());
    }

    /**
     * Converts this to a dynamic method invocation stub (for the invoke dynamic instruction).
     *
     * @param type        The type of access operation we're going to perform, see {@link MethodTypeReference}
     * @param isInterface Whether this is an interface method
     */
    public Invocation dynamicInvocation(@MagicConstant(valuesFromClass = MethodTypeReference.class) int type,
                                        boolean isInterface) {
        switch (type) {
            case MethodTypeReference.NEW_INVOKE_SPECIAL:
                if (!Objects.equals(signature.name(), "<init>"))
                    throw new IllegalArgumentException("Type 8 (NEW_INVOKE_SPECIAL) is reserved for constructor " +
                                                           "<init>");
            case MethodTypeReference.INVOKE_VIRTUAL, MethodTypeReference.INVOKE_STATIC,
                 MethodTypeReference.INVOKE_SPECIAL, MethodTypeReference.INVOKE_INTERFACE:
                if (Objects.equals(signature.name(), "<clinit>"))
                    throw new IllegalArgumentException("Cannot call the <clinit> class initializer");
        }
        return new Invocation(this, type, isInterface);
    }

    @Override
    public Member constant() {
        return this;
    }

    @Override
    public Type returnType() {
        return Type.fromDescriptor(this);
    }

    @Override
    public String name() {
        return signature.name();
    }

    @Override
    public Type[] parameters() {
        return Type.parameters(this);
    }

    @Override
    public String descriptorString() {
        return signature.descriptorString();
    }

    public boolean isField() {
        return !signature.descriptorString().startsWith("(");
    }

    /**
     * The data needed for a dynamic method invocation (using the invoke dynamic instruction).
     *
     * @param member      The thing we're accessing (e.g. a method or field)
     * @param type        The type of access operation we're going to perform, see {@link MethodTypeReference}
     * @param isInterface Whether this is an interface method
     */
    public record Invocation(Member member, @MagicConstant(valuesFromClass = MethodTypeReference.class) int type,
                             boolean isInterface) implements Descriptor, RecordConstant {

        @Override
        public String descriptorString() {
            return member.descriptorString();
        }

    }

}
