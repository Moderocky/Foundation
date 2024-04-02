package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.assembler.code.OpCode;
import mx.kenzie.foundation.assembler.tool.CodeBuilder;

import java.lang.invoke.TypeDescriptor;

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
    Array ARRAY = new Array();
    Switch SWITCH = new Switch();
    Input<Object> THIS = LOAD_VAR.self();
    Instruction.Input<Void> NULL = builder -> builder.write(OpCode.ACONST_NULL);
    Instruction.Input<Integer> ZERO = builder -> builder.write(OpCode.ICONST_0),
        FALSE = builder -> builder.write(OpCode.ICONST_0),
        ONE = builder -> builder.write(OpCode.ICONST_1),
        TRUE = builder -> builder.write(OpCode.ICONST_1);
    Push PUSH = new Push();
    Instruction.Block BREAK = (builder, block) -> builder.write(OpCode.GOTO.jump(block.end));

    void write(CodeBuilder builder);

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
        INT_TO_BYTE(OpCode.I2B),
        INT_TO_SHORT(OpCode.I2S),
        INT_TO_LONG(OpCode.I2L),
        INT_TO_FLOAT(OpCode.I2F),
        INT_TO_DOUBLE(OpCode.I2D),
        LONG_TO_INT(OpCode.L2I),
        LONG_TO_FLOAT(OpCode.L2F),
        LONG_TO_DOUBLE(OpCode.L2D),
        FLOAT_TO_INT(OpCode.F2I),
        FLOAT_TO_LONG(OpCode.F2L),
        FLOAT_TO_DOUBLE(OpCode.F2D),
        DOUBLE_TO_INT(OpCode.D2I),
        DOUBLE_TO_LONG(OpCode.D2L),
        DOUBLE_TO_FLOAT(OpCode.D2F);
        public final mx.kenzie.foundation.assembler.code.Instruction opcode;

        Convert(mx.kenzie.foundation.assembler.code.Instruction opcode) {
            this.opcode = opcode;
        }
    }

    interface Base extends Instruction {

    }

    interface Input<Type> extends Instruction {

        @SuppressWarnings("unchecked")
        default <Other, Klass extends java.lang.reflect.Type & TypeDescriptor> Input<Other> cast(Klass type) {
            return (Input<Other>) CAST.object((Input<Object>) this, type);
        }

    }

    interface Block {

        void write(CodeBuilder builder, mx.kenzie.foundation.Block block);

    }

}
