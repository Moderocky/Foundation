package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.U2;
import mx.kenzie.foundation.assembler.UVec;
import org.valross.constantine.RecordConstant;

public record Deprecated(U2 attribute_name_index)
    implements ZeroAttribute, CodeAttributeInfo, AttributeInfo, UVec, RecordConstant {

}
