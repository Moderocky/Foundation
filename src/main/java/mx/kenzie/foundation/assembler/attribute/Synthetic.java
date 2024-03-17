package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.U2;
import mx.kenzie.foundation.assembler.U4;
import mx.kenzie.foundation.assembler.UVec;
import org.valross.constantine.RecordConstant;

public record Synthetic(U2 attribute_name_index)
    implements CodeAttributeInfo, AttributeInfo, UVec, RecordConstant {

    @Override
    public U4 attribute_length() {
        return U4.valueOf(0);
    }

    @Override
    public UVec info() {
        return this.attribute_length(); // re-use the zero value
    }

}
