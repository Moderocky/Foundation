package org.valross.foundation.assembler.constant;

public interface MethodTypeReference {

    int GET_FIELD = 1; // getfield C.f:T
    int GET_STATIC = 2; // getstatic C.f:T
    int PUT_FIELD = 3; // putfield C.f:T
    int PUT_STATIC = 4; // putstatic C.f:T
    int INVOKE_VIRTUAL = 5; // invokevirtual C.m:(A*)T
    int INVOKE_STATIC = 6; // invokestatic C.m:(A*)T
    int INVOKE_SPECIAL = 7; // invokespecial C.m:(A*)T
    int NEW_INVOKE_SPECIAL = 8; // new C; dup; invokespecial C.<init>:(A*)V
    int INVOKE_INTERFACE = 9; // invokeinterface C.m:(A*)T

}
