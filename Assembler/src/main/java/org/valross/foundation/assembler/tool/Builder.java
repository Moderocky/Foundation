package org.valross.foundation.assembler.tool;

interface Builder {

    /**
     * Notifies this builder that it will be used in its current state (i.e. without anything added or modified).
     * This is an opportunity to update, correct or bake any references, as well as to make sure everything is ordered
     * correctly.
     */
    void finalise();

}
