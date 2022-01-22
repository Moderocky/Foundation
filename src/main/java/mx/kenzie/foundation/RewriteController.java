package mx.kenzie.foundation;

public abstract class RewriteController {
    
    public abstract boolean isInline();
    
    public abstract void markReturn();
    
    public abstract void useField(Type owner, String name);
    
    public abstract void useMethod(Type owner, String name);
    
    public WriteInstruction store(int opcode, int slot) {
        final int i = adjustVariable(slot);
        return (writer, visitor) -> {
            visitor.visitVarInsn(opcode, i);
        };
    }
    
    public abstract int adjustVariable(int slot);
    
    public WriteInstruction load(int opcode, int slot) {
        final int i = adjustVariable(slot);
        return (writer, visitor) -> {
            visitor.visitVarInsn(opcode, i);
        };
    }
    
    public WriteInstruction return0(int opcode) {
        if (opcode < 177) {
            final int slot = returnSlot();
            return (writer, visitor) -> {
                visitor.visitVarInsn(opcode - 118, slot);
                jumpToEnd().accept(writer, visitor);
            };
        }
        return jumpToEnd();
    }
    
    public abstract int returnSlot();
    
    public abstract WriteInstruction jumpToEnd();
    
    public abstract void write(String mode, int opcode, Object data);
    
    public abstract WriteInstruction end();
    
}
