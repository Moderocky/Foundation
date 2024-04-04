package org.valross.foundation.assembler.tool;

/**
 * Something that should be purged from storage if it is discarded,
 * e.g. to remove unused constants or references.
 */
public interface Purgative {

    void purge(ClassFileBuilder.Storage storage);

}
