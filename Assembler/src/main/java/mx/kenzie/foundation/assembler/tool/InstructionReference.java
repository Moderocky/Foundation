package mx.kenzie.foundation.assembler.tool;

import mx.kenzie.foundation.assembler.code.CodeElement;

import java.lang.ref.WeakReference;

/**
 * A reference to an instruction in a code vector.
 * Note that this will only work for unique instructions, and re-usable ones (e.g. ACONST_NULL) will simply
 * return the first instance in the code vector.
 */
public class InstructionReference extends TableReference<CodeElement> {

    public InstructionReference(Iterable<CodeElement> pool, CodeElement value) {
        super(pool, new WeakReference<>(value));
    }

    @Override
    public int index() {
        final CodeElement element = reference.get();
        if (element == null) return -1; // it was discarded :(
        int index = 0;
        for (CodeElement value : pool) {
            if (element == value) return index; // we want to know if it's EXACTLY our reference
            index += value.length();
        }
        return -1; // was it ever there in the first place?
    }

}
