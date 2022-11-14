package mx.kenzie.foundation.instruction;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@FunctionalInterface
public interface Instruction {
    
    LoadVariable LOAD_VAR = new LoadVariable();
    StoreVariable STORE_VAR = new StoreVariable();
    Return RETURN = new Return();
    LoadConstant CONSTANT = new LoadConstant();
    AccessField FIELD = new AccessField();
    CallMethod METHOD = new CallMethod();
    CallSuper SUPER = new CallSuper();
    CallConstructor NEW = new CallConstructor();
    ThrowError THROW = new ThrowError();
    Conditional IF = new Conditional();
    While WHILE = new While();
    ForEach FOR = new ForEach();
    Equals EQUALS = new Equals();
    Binary COMPARE = new Binary();
    Sum SUM = new Sum();
    Increment INCREMENT = new Increment();
    Negate NOT = new Negate();
    Instruction.Input NULL = visitor -> visitor.visitInsn(Opcodes.ACONST_NULL);
    Instruction.Input ZERO = visitor -> visitor.visitInsn(Opcodes.ICONST_0),
        FALSE = visitor -> visitor.visitInsn(Opcodes.ICONST_0),
        ONE = visitor -> visitor.visitInsn(Opcodes.ICONST_1),
        TRUE = visitor -> visitor.visitInsn(Opcodes.ICONST_1);
    PushByte BYTE = new PushByte();
    PushShort SHORT = new PushShort();
    Instruction.Block BREAK = (visitor, block) -> {visitor.visitJumpInsn(Opcodes.GOTO, block.end);};
    
    void write(MethodVisitor visitor);
    
    enum Operator {
        EQ,
        LESS,
        GREATER,
        LESS_EQ,
        GREATER_EQ,
        NOT_EQ,
        AND,
        OR,
        XOR,
    }
    
    enum Math {
        PLUS,
        MINUS,
        TIMES,
        DIVIDED
    }
    
    interface Base extends Instruction {
    
    }
    
    interface Input extends Instruction {
    
    }
    
    interface Block {
        void write(MethodVisitor visitor, mx.kenzie.foundation.Block block);
    }
    
}
