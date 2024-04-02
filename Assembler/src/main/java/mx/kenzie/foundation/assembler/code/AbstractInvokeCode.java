package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.detail.Member;

import java.lang.invoke.TypeDescriptor;

public interface AbstractInvokeCode extends OpCode {

    default <Klass extends java.lang.reflect.Type & TypeDescriptor>
    UnboundedElement method(boolean isInterface, Klass owner, Klass returnType, String name, Klass... parameters) {
        if (isInterface) return this.interfaceMethod(new Member(owner, returnType, name, parameters));
        return this.method(new Member(owner, returnType, name, parameters));
    }

    default <Klass extends java.lang.reflect.Type & TypeDescriptor>
    UnboundedElement method(Klass owner, Klass returnType, String name, Klass... parameters) {
        return this.method(new Member(owner, returnType, name, parameters));
    }

    default <Klass extends java.lang.reflect.Type & TypeDescriptor>
    UnboundedElement interfaceMethod(Klass owner, Klass returnType, String name, Klass... parameters) {
        return this.interfaceMethod(new Member(owner, returnType, name, parameters));
    }

    UnboundedElement method(Member member);

    UnboundedElement interfaceMethod(Member member);

}
