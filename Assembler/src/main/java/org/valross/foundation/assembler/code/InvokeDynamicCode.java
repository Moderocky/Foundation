package org.valross.foundation.assembler.code;

import org.valross.foundation.assembler.constant.ConstantPoolInfo;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.UVec;
import org.valross.foundation.detail.DynamicReference;
import org.valross.foundation.detail.Member;
import org.valross.foundation.detail.Signature;

import java.lang.constant.Constable;

/**
 * An opcode for invoking a dynamic method instruction via its constant call site.
 * This obtains the call site using the invocation pattern and the constant arguments
 * (see {@link #method(Signature, Member.Invocation, Constable...)}), disguising it as a method with the provided
 * signature, then invokes that using the arguments on the stack matching the apparent signature.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record InvokeDynamicCode(String mnemonic, byte code) implements OpCode {

    public CodeElement method(PoolReference reference) {
        return CodeElement.vector(code, UVec.of(reference, U2.ZERO));
    }

    public UnboundedElement method(Signature signature, Member.Invocation invocation, Constable... arguments) {
        final DynamicReference reference = new DynamicReference(DynamicReference.Kind.INVOCATION, signature,
                                                                invocation, arguments);
        return this.method(reference);
    }

    public UnboundedElement method(DynamicReference reference) {
        return storage -> this.method(storage.constant(ConstantPoolInfo.INVOKE_DYNAMIC, reference));
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
    }

    @Override
    public int length() {
        return 5;
    }

}
