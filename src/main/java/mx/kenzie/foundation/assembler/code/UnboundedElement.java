package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;

/**
 * All the data required to assemble an instruction/element in a code attribute.
 * However, this data is 'unbounded' (i.e. not assigned to anything in particular)
 * and so does not have any references to the constant pool, the frame or the local type.
 */
public interface UnboundedElement {

    CodeElement bound(ClassFileBuilder.Storage storage);

}
