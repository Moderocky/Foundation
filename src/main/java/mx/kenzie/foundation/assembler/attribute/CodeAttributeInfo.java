package mx.kenzie.foundation.assembler.attribute;

/**
 * An attribute that can be placed on a code block.
 * Note that this is for ANYTHING that can be a code attribute (even if it can also be a file attribute, etc.)
 * and is used simply to prevent non-code attributes from being accidentally listed.
 */
public interface CodeAttributeInfo extends AttributeInfo {

}
