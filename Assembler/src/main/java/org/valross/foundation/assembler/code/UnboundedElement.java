package org.valross.foundation.assembler.code;

import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.CodePoint;

/**
 * All the data required to assemble an instruction/element in a code attribute.
 * However, this data is 'unbounded' (i.e. not assigned to anything in particular)
 * and so does not have any references to the constant pool, the frame or the local type.
 */
public interface UnboundedElement extends CodePoint {

    CodeElement bound(ClassFileBuilder.Storage storage);

    default @Override void addTo(CodeBuilder builder) {
        builder.write(this);
    }

}
