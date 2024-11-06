package org.valross.foundation.assembler.code;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.detail.TypeHint;

public record NullCode(TypeHint hint) implements RecordConstant, SingleInstruction {

    @Override
    public String mnemonic() {
        return "ACONST_NULL";
    }

    @Override
    public byte code() {
        return Codes.ACONST_NULL;
    }

    @Override
    public void notify(CodeBuilder builder) {
        if (builder.trackStack()) builder.stack().push(hint);
    }

    public NullCode as(TypeHint hint) {
        return new NullCode(hint);
    }

}
