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
    Cast CAST = new Cast();
    Instruction.Input<Void> NULL = visitor -> visitor.visitInsn(Opcodes.ACONST_NULL);
    Instruction.Input<Integer> ZERO = visitor -> visitor.visitInsn(Opcodes.ICONST_0),
        FALSE = visitor -> visitor.visitInsn(Opcodes.ICONST_0),
        ONE = visitor -> visitor.visitInsn(Opcodes.ICONST_1),
        TRUE = visitor -> visitor.visitInsn(Opcodes.ICONST_1);
    Push PUSH = new Push();
    Instruction.Block BREAK = (visitor, block) -> visitor.visitJumpInsn(Opcodes.GOTO, block.end);

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

    enum Convert {
        INT_TO_BYTE(Opcodes.I2B),
        INT_TO_SHORT(Opcodes.I2S),
        INT_TO_LONG(Opcodes.I2L),
        INT_TO_FLOAT(Opcodes.I2F),
        INT_TO_DOUBLE(Opcodes.I2D),
        LONG_TO_INT(Opcodes.L2I),
        LONG_TO_FLOAT(Opcodes.L2F),
        LONG_TO_DOUBLE(Opcodes.L2D),
        FLOAT_TO_INT(Opcodes.F2I),
        FLOAT_TO_LONG(Opcodes.F2L),
        FLOAT_TO_DOUBLE(Opcodes.F2D),
        DOUBLE_TO_INT(Opcodes.D2I),
        DOUBLE_TO_LONG(Opcodes.D2L),
        DOUBLE_TO_FLOAT(Opcodes.D2F);
        public final int opcode;

        Convert(int opcode) {
            this.opcode = opcode;
        }
    }

    interface Base extends Instruction {

    }

    interface Input<Type> extends Instruction {

    }

    interface Block {
        void write(MethodVisitor visitor, mx.kenzie.foundation.Block block);
    }

}
