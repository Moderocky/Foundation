package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.vector.U1;
import mx.kenzie.foundation.assembler.vector.UVec;

/**
 * An operation code starts an instruction in the virtual machine.
 * Some operation codes (opcodes) will be just the code with no other data,
 * while others are followed by the data needed to complete the instruction (e.g. "load variable (variable index)").
 * Most instructions use, add to or modify the stack.
 * The opcodes here can be switched using their byte `code()` method, using the constants in {@link Codes}
 * as the switch cases.
 */
public interface OpCode {

    Instruction AALOAD = new Instruction("AALOAD", Codes.AALOAD);
    Instruction AASTORE = new Instruction("AASTORE", Codes.AASTORE);
    Instruction ACONST_NULL = new Instruction("ACONST_NULL", Codes.ACONST_NULL);
    Instruction ALOAD_0 = new Instruction("ALOAD_0", Codes.ALOAD_0);
    Instruction ALOAD_1 = new Instruction("ALOAD_1", Codes.ALOAD_1);
    Instruction ALOAD_2 = new Instruction("ALOAD_2", Codes.ALOAD_2);
    Instruction ALOAD_3 = new Instruction("ALOAD_3", Codes.ALOAD_3);
    VariableCode ALOAD = new VariableCode("ALOAD", Codes.ALOAD) {
        @Override
        public CodeElement var(int slot) {
            return switch (slot) {
                case 0 -> ALOAD_0;
                case 1 -> ALOAD_1;
                case 2 -> ALOAD_2;
                case 3 -> ALOAD_3;
                default -> super.var(slot);
            };
        }
    };
    TypeCode.Array ANEWARRAY = new TypeCode.Array("ANEWARRAY", Codes.ANEWARRAY);
    Instruction ARETURN = new Instruction("ARETURN", Codes.ARETURN);
    Instruction ARRAYLENGTH = new Instruction("ARRAYLENGTH", Codes.ARRAYLENGTH);
    Instruction ASTORE_0 = new Instruction("ASTORE_0", Codes.ASTORE_0);
    Instruction ASTORE_1 = new Instruction("ASTORE_1", Codes.ASTORE_1);
    Instruction ASTORE_2 = new Instruction("ASTORE_2", Codes.ASTORE_2);
    Instruction ASTORE_3 = new Instruction("ASTORE_3", Codes.ASTORE_3);
    VariableCode ASTORE = new VariableCode("ASTORE", Codes.ASTORE) {
        @Override
        public CodeElement var(int slot) {
            return switch (slot) {
                case 0 -> ASTORE_0;
                case 1 -> ASTORE_1;
                case 2 -> ASTORE_2;
                case 3 -> ASTORE_3;
                default -> super.var(slot);
            };
        }
    };
    Instruction ATHROW = new Instruction("ATHROW", Codes.ATHROW);
    Instruction BALOAD = new Instruction("BALOAD", Codes.BALOAD);
    Instruction BASTORE = new Instruction("BASTORE", Codes.BASTORE);
    PushCode BIPUSH = new PushCode("BIPUSH", Codes.BIPUSH);
    Instruction CALOAD = new Instruction("CALOAD", Codes.CALOAD);
    Instruction CASTORE = new Instruction("CASTORE", Codes.CASTORE);
    TypeCode CHECKCAST = new TypeCode("CHECKCAST", Codes.CHECKCAST);
    Instruction D2F = new Instruction("D2F", Codes.D2F);
    Instruction D2I = new Instruction("D2I", Codes.D2I);
    Instruction D2L = new Instruction("D2L", Codes.D2L);
    Instruction DADD = new Instruction("DADD", Codes.DADD);
    Instruction DALOAD = new Instruction("DALOAD", Codes.DALOAD);
    Instruction DASTORE = new Instruction("DASTORE", Codes.DASTORE);
    Instruction DCMPG = new Instruction("DCMPG", Codes.DCMPG);
    Instruction DCMPL = new Instruction("DCMPL", Codes.DCMPL);
    Instruction DCONST_0 = new Instruction("DCONST_0", Codes.DCONST_0);
    Instruction DCONST_1 = new Instruction("DCONST_1", Codes.DCONST_1);
    Instruction DDIV = new Instruction("DDIV", Codes.DDIV);
    Instruction DLOAD_0 = new Instruction("DLOAD_0", Codes.DLOAD_0);
    Instruction DLOAD_1 = new Instruction("DLOAD_1", Codes.DLOAD_1);
    Instruction DLOAD_2 = new Instruction("DLOAD_2", Codes.DLOAD_2);
    Instruction DLOAD_3 = new Instruction("DLOAD_3", Codes.DLOAD_3);
    VariableCode DLOAD = new VariableCode("DLOAD", Codes.DLOAD) {
        @Override
        public CodeElement var(int slot) {
            return switch (slot) {
                case 0 -> DLOAD_0;
                case 1 -> DLOAD_1;
                case 2 -> DLOAD_2;
                case 3 -> DLOAD_3;
                default -> super.var(slot);
            };
        }
    };
    Instruction DMUL = new Instruction("DMUL", Codes.DMUL);
    Instruction DNEG = new Instruction("DNEG", Codes.DNEG);
    Instruction DREM = new Instruction("DREM", Codes.DREM);
    Instruction DRETURN = new Instruction("DRETURN", Codes.DRETURN);
    Instruction DSTORE_0 = new Instruction("DSTORE_0", Codes.DSTORE_0);
    Instruction DSTORE_1 = new Instruction("DSTORE_1", Codes.DSTORE_1);
    Instruction DSTORE_2 = new Instruction("DSTORE_2", Codes.DSTORE_2);
    Instruction DSTORE_3 = new Instruction("DSTORE_3", Codes.DSTORE_3);
    VariableCode DSTORE = new VariableCode("DSTORE", Codes.DSTORE) {
        @Override
        public CodeElement var(int slot) {
            return switch (slot) {
                case 0 -> DSTORE_0;
                case 1 -> DSTORE_1;
                case 2 -> DSTORE_2;
                case 3 -> DSTORE_3;
                default -> super.var(slot);
            };
        }
    };
    Instruction DSUB = new Instruction("DSUB", Codes.DSUB);
    Instruction DUP = new Instruction("DUP", Codes.DUP);
    Instruction DUP_X1 = new Instruction("DUP_X1", Codes.DUP_X1);
    Instruction DUP_X2 = new Instruction("DUP_X2", Codes.DUP_X2);
    Instruction DUP2 = new Instruction("DUP2", Codes.DUP2);
    Instruction DUP2_X1 = new Instruction("DUP2_X1", Codes.DUP2_X1);
    Instruction DUP2_X2 = new Instruction("DUP2_X2", Codes.DUP2_X2);
    Instruction F2D = new Instruction("F2D", Codes.F2D);
    Instruction F2I = new Instruction("F2I", Codes.F2I);
    Instruction F2L = new Instruction("F2L", Codes.F2L);
    Instruction FADD = new Instruction("FADD", Codes.FADD);
    Instruction FALOAD = new Instruction("FALOAD", Codes.FALOAD);
    Instruction FASTORE = new Instruction("FASTORE", Codes.FASTORE);
    Instruction FCMPG = new Instruction("FCMPG", Codes.FCMPG);
    Instruction FCMPL = new Instruction("FCMPL", Codes.FCMPL);
    Instruction FCONST_0 = new Instruction("FCONST_0", Codes.FCONST_0);
    Instruction FCONST_1 = new Instruction("FCONST_1", Codes.FCONST_1);
    Instruction FCONST_2 = new Instruction("FCONST_2", Codes.FCONST_2);
    Instruction FDIV = new Instruction("FDIV", Codes.FDIV);
    Instruction FLOAD_0 = new Instruction("FLOAD_0", Codes.FLOAD_0);
    Instruction FLOAD_1 = new Instruction("FLOAD_1", Codes.FLOAD_1);
    Instruction FLOAD_2 = new Instruction("FLOAD_2", Codes.FLOAD_2);
    Instruction FLOAD_3 = new Instruction("FLOAD_3", Codes.FLOAD_3);
    VariableCode FLOAD = new VariableCode("FLOAD", Codes.FLOAD) {
        @Override
        public CodeElement var(int slot) {
            return switch (slot) {
                case 0 -> FLOAD_0;
                case 1 -> FLOAD_1;
                case 2 -> FLOAD_2;
                case 3 -> FLOAD_3;
                default -> super.var(slot);
            };
        }
    };
    Instruction FMUL = new Instruction("FMUL", Codes.FMUL);
    Instruction FNEG = new Instruction("FNEG", Codes.FNEG);
    Instruction FREM = new Instruction("FREM", Codes.FREM);
    Instruction FRETURN = new Instruction("FRETURN", Codes.FRETURN);
    Instruction FSTORE_0 = new Instruction("FSTORE_0", Codes.FSTORE_0);
    Instruction FSTORE_1 = new Instruction("FSTORE_1", Codes.FSTORE_1);
    Instruction FSTORE_2 = new Instruction("FSTORE_2", Codes.FSTORE_2);
    Instruction FSTORE_3 = new Instruction("FSTORE_3", Codes.FSTORE_3);
    VariableCode FSTORE = new VariableCode("FSTORE", Codes.FSTORE) {
        @Override
        public CodeElement var(int slot) {
            return switch (slot) {
                case 0 -> FSTORE_0;
                case 1 -> FSTORE_1;
                case 2 -> FSTORE_2;
                case 3 -> FSTORE_3;
                default -> super.var(slot);
            };
        }
    };
    Instruction FSUB = new Instruction("FSUB", Codes.FSUB);
    FieldCode GETFIELD = new FieldCode("GETFIELD", Codes.GETFIELD);
    FieldCode GETSTATIC = new FieldCode("GETSTATIC", Codes.GETSTATIC);
    UncheckedCode GOTO = new UncheckedCode("GOTO", Codes.GOTO);
    UncheckedCode GOTO_W = new UncheckedCode("GOTO_W", Codes.GOTO_W);
    Instruction I2B = new Instruction("I2B", Codes.I2B);
    Instruction I2C = new Instruction("I2C", Codes.I2C);
    Instruction I2D = new Instruction("I2D", Codes.I2D);
    Instruction I2F = new Instruction("I2F", Codes.I2F);
    Instruction I2L = new Instruction("I2L", Codes.I2L);
    Instruction I2S = new Instruction("I2S", Codes.I2S);
    Instruction IADD = new Instruction("IADD", Codes.IADD);
    Instruction IALOAD = new Instruction("IALOAD", Codes.IALOAD);
    Instruction IAND = new Instruction("IAND", Codes.IAND);
    Instruction IASTORE = new Instruction("IASTORE", Codes.IASTORE);
    Instruction ICONST_M1 = new Instruction("ICONST_M1", Codes.ICONST_M1);
    Instruction ICONST_0 = new Instruction("ICONST_0", Codes.ICONST_0);
    Instruction ICONST_1 = new Instruction("ICONST_1", Codes.ICONST_1);
    Instruction ICONST_2 = new Instruction("ICONST_2", Codes.ICONST_2);
    Instruction ICONST_3 = new Instruction("ICONST_3", Codes.ICONST_3);
    Instruction ICONST_4 = new Instruction("ICONST_4", Codes.ICONST_4);
    Instruction ICONST_5 = new Instruction("ICONST_5", Codes.ICONST_5);
    Instruction IDIV = new Instruction("IDIV", Codes.IDIV);
    Instruction IF_ACMPEQ = new Instruction("IF_ACMPEQ", Codes.IF_ACMPEQ);
    Instruction IF_ACMPNE = new Instruction("IF_ACMPNE", Codes.IF_ACMPNE);
    Instruction IF_ICMPEQ = new Instruction("IF_ICMPEQ", Codes.IF_ICMPEQ);
    Instruction IF_ICMPNE = new Instruction("IF_ICMPNE", Codes.IF_ICMPNE);
    Instruction IF_ICMPLT = new Instruction("IF_ICMPLT", Codes.IF_ICMPLT);
    Instruction IF_ICMPGE = new Instruction("IF_ICMPGE", Codes.IF_ICMPGE);
    Instruction IF_ICMPGT = new Instruction("IF_ICMPGT", Codes.IF_ICMPGT);
    Instruction IF_ICMPLE = new Instruction("IF_ICMPLE", Codes.IF_ICMPLE);
    Instruction IFEQ = new Instruction("IFEQ", Codes.IFEQ);
    Instruction IFNE = new Instruction("IFNE", Codes.IFNE);
    Instruction IFLT = new Instruction("IFLT", Codes.IFLT);
    Instruction IFGE = new Instruction("IFGE", Codes.IFGE);
    Instruction IFGT = new Instruction("IFGT", Codes.IFGT);
    Instruction IFLE = new Instruction("IFLE", Codes.IFLE);
    UncheckedCode IFNONNULL = new UncheckedCode("IFNONNULL", Codes.IFNONNULL);
    UncheckedCode IFNULL = new UncheckedCode("IFNULL", Codes.IFNULL);
    IncrementCode IINC = new IncrementCode("IINC", Codes.IINC);
    Instruction ILOAD_0 = new Instruction("ILOAD_0", Codes.ILOAD_0);
    Instruction ILOAD_1 = new Instruction("ILOAD_1", Codes.ILOAD_1);
    Instruction ILOAD_2 = new Instruction("ILOAD_2", Codes.ILOAD_2);
    Instruction ILOAD_3 = new Instruction("ILOAD_3", Codes.ILOAD_3);
    VariableCode ILOAD = new VariableCode("ILOAD", Codes.ILOAD) {
        @Override
        public CodeElement var(int slot) {
            return switch (slot) {
                case 0 -> ILOAD_0;
                case 1 -> ILOAD_1;
                case 2 -> ILOAD_2;
                case 3 -> ILOAD_3;
                default -> super.var(slot);
            };
        }
    };
    Instruction IMUL = new Instruction("IMUL", Codes.IMUL);
    Instruction INEG = new Instruction("INEG", Codes.INEG);
    TypeCode INSTANCEOF = new TypeCode("INSTANCEOF", Codes.INSTANCEOF);
    InvokeDynamicCode INVOKEDYNAMIC = new InvokeDynamicCode("INVOKEDYNAMIC", Codes.INVOKEDYNAMIC);
    InvokeInterfaceCode INVOKEINTERFACE = new InvokeInterfaceCode("INVOKEINTERFACE", Codes.INVOKEINTERFACE);
    InvokeCode INVOKESPECIAL = new InvokeCode("INVOKESPECIAL", Codes.INVOKESPECIAL);
    InvokeCode INVOKESTATIC = new InvokeCode("INVOKESTATIC", Codes.INVOKESTATIC);
    InvokeCode INVOKEVIRTUAL = new InvokeCode("INVOKEVIRTUAL", Codes.INVOKEVIRTUAL);
    Instruction IOR = new Instruction("IOR", Codes.IOR);
    Instruction IREM = new Instruction("IREM", Codes.IREM);
    Instruction IRETURN = new Instruction("IRETURN", Codes.IRETURN);
    Instruction ISHL = new Instruction("ISHL", Codes.ISHL);
    Instruction ISHR = new Instruction("ISHR", Codes.ISHR);
    Instruction ISTORE_0 = new Instruction("ISTORE_0", Codes.ISTORE_0);
    Instruction ISTORE_1 = new Instruction("ISTORE_1", Codes.ISTORE_1);
    Instruction ISTORE_2 = new Instruction("ISTORE_2", Codes.ISTORE_2);
    Instruction ISTORE_3 = new Instruction("ISTORE_3", Codes.ISTORE_3);
    VariableCode ISTORE = new VariableCode("ISTORE", Codes.ISTORE) {
        @Override
        public CodeElement var(int slot) {
            return switch (slot) {
                case 0 -> ISTORE_0;
                case 1 -> ISTORE_1;
                case 2 -> ISTORE_2;
                case 3 -> ISTORE_3;
                default -> super.var(slot);
            };
        }
    };
    Instruction ISUB = new Instruction("ISUB", Codes.ISUB);
    Instruction IUSHR = new Instruction("IUSHR", Codes.IUSHR);
    Instruction IXOR = new Instruction("IXOR", Codes.IXOR);
    UncheckedCode JSR = new UncheckedCode("JSR", Codes.JSR);
    UncheckedCode JSR_W = new UncheckedCode("JSR_W", Codes.JSR_W);
    Instruction L2D = new Instruction("L2D", Codes.L2D);
    Instruction L2F = new Instruction("L2F", Codes.L2F);
    Instruction L2I = new Instruction("L2I", Codes.L2I);
    Instruction LADD = new Instruction("LADD", Codes.LADD);
    Instruction LALOAD = new Instruction("LALOAD", Codes.LALOAD);
    Instruction LAND = new Instruction("LAND", Codes.LAND);
    Instruction LASTORE = new Instruction("LASTORE", Codes.LASTORE);
    Instruction LCMP = new Instruction("LCMP", Codes.LCMP);
    Instruction LCONST_0 = new Instruction("LCONST_0", Codes.LCONST_0);
    Instruction LCONST_1 = new Instruction("LCONST_1", Codes.LCONST_1);
    LoadConstantCode LDC = new LoadConstantCode("LDC", Codes.LDC);
    UncheckedCode LDC_W = new UncheckedCode("LDC_W", Codes.LDC_W);
    UncheckedCode LDC2_W = new UncheckedCode("LDC2_W", Codes.LDC2_W);
    Instruction LDIV = new Instruction("LDIV", Codes.LDIV);
    Instruction LLOAD_0 = new Instruction("LLOAD_0", Codes.LLOAD_0);
    Instruction LLOAD_1 = new Instruction("LLOAD_1", Codes.LLOAD_1);
    Instruction LLOAD_2 = new Instruction("LLOAD_2", Codes.LLOAD_2);
    Instruction LLOAD_3 = new Instruction("LLOAD_3", Codes.LLOAD_3);
    VariableCode LLOAD = new VariableCode("LLOAD", Codes.LLOAD) {
        @Override
        public CodeElement var(int slot) {
            return switch (slot) {
                case 0 -> LLOAD_0;
                case 1 -> LLOAD_1;
                case 2 -> LLOAD_2;
                case 3 -> LLOAD_3;
                default -> super.var(slot);
            };
        }
    };
    Instruction LMUL = new Instruction("LMUL", Codes.LMUL);
    Instruction LNEG = new Instruction("LNEG", Codes.LNEG);
    UncheckedCode LOOKUPSWITCH = new UncheckedCode("LOOKUPSWITCH", Codes.LOOKUPSWITCH);
    Instruction LOR = new Instruction("LOR", Codes.LOR);
    Instruction LREM = new Instruction("LREM", Codes.LREM);
    Instruction LRETURN = new Instruction("LRETURN", Codes.LRETURN);
    Instruction LSHL = new Instruction("LSHL", Codes.LSHL);
    Instruction LSHR = new Instruction("LSHR", Codes.LSHR);
    Instruction LSTORE_0 = new Instruction("LSTORE_0", Codes.LSTORE_0);
    Instruction LSTORE_1 = new Instruction("LSTORE_1", Codes.LSTORE_1);
    Instruction LSTORE_2 = new Instruction("LSTORE_2", Codes.LSTORE_2);
    Instruction LSTORE_3 = new Instruction("LSTORE_3", Codes.LSTORE_3);
    VariableCode LSTORE = new VariableCode("LSTORE", Codes.LSTORE) {
        @Override
        public CodeElement var(int slot) {
            return switch (slot) {
                case 0 -> LSTORE_0;
                case 1 -> LSTORE_1;
                case 2 -> LSTORE_2;
                case 3 -> LSTORE_3;
                default -> super.var(slot);
            };
        }
    };
    Instruction LSUB = new Instruction("LSUB", Codes.LSUB);
    Instruction LUSHR = new Instruction("LUSHR", Codes.LUSHR);
    Instruction LXOR = new Instruction("LXOR", Codes.LXOR);
    Instruction MONITORENTER = new Instruction("MONITORENTER", Codes.MONITORENTER);
    Instruction MONITOREXIT = new Instruction("MONITOREXIT", Codes.MONITOREXIT);
    TypeCode.MultiArray MULTIANEWARRAY = new TypeCode.MultiArray("MULTIANEWARRAY", Codes.MULTIANEWARRAY);
    TypeCode NEW = new TypeCode("NEW", Codes.NEW);
    TypeCode.Array NEWARRAY = new TypeCode.Array("NEWARRAY", Codes.NEWARRAY);
    Instruction NOP = new Instruction("NOP", Codes.NOP);
    Instruction POP = new Instruction("POP", Codes.POP);
    Instruction POP2 = new Instruction("POP2", Codes.POP2);
    FieldCode PUTFIELD = new FieldCode("PUTFIELD", Codes.PUTFIELD);
    FieldCode PUTSTATIC = new FieldCode("PUTSTATIC", Codes.PUTSTATIC);
    VariableCode RET = new VariableCode("RET", Codes.RET) {};
    Instruction RETURN = new Instruction("RETURN", Codes.RETURN);
    Instruction SALOAD = new Instruction("SALOAD", Codes.SALOAD);
    Instruction SASTORE = new Instruction("SASTORE", Codes.SASTORE);
    PushCode SIPUSH = new PushCode("SIPUSH", Codes.SIPUSH);
    Instruction SWAP = new Instruction("SWAP", Codes.SWAP);
    UncheckedCode TABLESWITCH = new UncheckedCode("TABLESWITCH", Codes.TABLESWITCH);
    WideCode WIDE = new WideCode("WIDE", Codes.WIDE);

    static OpCode[] opcodes() {
        return Codes.getAllOpcodes();
    }

    static OpCode getCode(int opcode) {
        return Codes.getAllOpcodes()[opcode];
    }

    /**
     * The 'mnemonic' (reference name) of this instruction.
     * This has no actual presence in a compiled program, but is useful for debugging purposes.
     * These names ought to have parity with the constants in {@link Codes}.
     *
     * @return The name of the instruction.
     */
    String mnemonic();

    /**
     * The raw numeric operation code for this instruction.
     * Note that this byte shouldn't be signed, i.e. it needs to be unpacked with {@link Byte#toUnsignedInt(byte)}
     * in order to be read correctly.
     *
     * @return The opcode, in a signed byte.
     */
    byte code();

    int length();

    default boolean hasFixedLength() {
        return this.length() > 0;
    }

    default CodeElement raw(UVec data) {
        return new UncheckedElement(this.code(), data);
    }

    default CodeElement raw(int unsignedByte) {
        return new UncheckedElement(this.code(), U1.valueOf(unsignedByte));
    }

}
