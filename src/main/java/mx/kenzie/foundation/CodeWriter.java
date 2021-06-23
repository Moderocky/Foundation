package mx.kenzie.foundation;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.*;

public class CodeWriter {
    
    List<WriteInstruction> instructions = new ArrayList<>();
    Map<String, Label> labels = new HashMap<>();
    
    void addInstruction(final WriteInstruction... instructions) {
        this.instructions.addAll(Arrays.asList(instructions));
    }
    
    void write(MethodVisitor visitor) {
        for (WriteInstruction instruction : instructions) {
            instruction.accept(this, visitor);
        }
    }
    
    boolean isEmpty() {
        return instructions.isEmpty();
    }
    
}
