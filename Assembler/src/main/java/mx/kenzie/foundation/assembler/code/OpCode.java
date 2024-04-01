package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.StackNotifier;
import mx.kenzie.foundation.assembler.tool.VariableNotifier;
import mx.kenzie.foundation.assembler.vector.U1;
import mx.kenzie.foundation.assembler.vector.UVec;
import mx.kenzie.foundation.detail.Type;
import mx.kenzie.foundation.detail.TypeHint;

/**
 * An operation code starts an instruction in the virtual machine.
 * Some operation codes (opcodes) will be just the code with no other data,
 * while others are followed by the data needed to complete the instruction (e.g. "load variable (variable index)").
 * Most instructions use, add to or modify the stack.
 * The opcodes here can be switched using their byte `code()` method, using the constants in {@link Codes}
 * as the switch cases.
 */
public interface OpCode {

    Instruction AALOAD = new Instruction("AALOAD", Codes.AALOAD, (stack, _) -> {
        stack.pop();
        stack.push(stack.pop().asType().componentType());
    });
    Instruction AASTORE = new Instruction("AASTORE", Codes.AASTORE, (stack, _) -> stack.pop(3));
    Instruction ACONST_NULL = new Instruction("ACONST_NULL", Codes.ACONST_NULL, StackNotifier.push(TypeHint.none()));
    Instruction ALOAD_0 = new Instruction("ALOAD_0", Codes.ALOAD_0, StackNotifier.pushVariable(0));
    Instruction ALOAD_1 = new Instruction("ALOAD_1", Codes.ALOAD_1, StackNotifier.pushVariable(1));
    Instruction ALOAD_2 = new Instruction("ALOAD_2", Codes.ALOAD_2, StackNotifier.pushVariable(2));
    Instruction ALOAD_3 = new Instruction("ALOAD_3", Codes.ALOAD_3, StackNotifier.pushVariable(3));
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
    Instruction ARETURN = new Instruction("ARETURN", Codes.ARETURN, (stack, _) -> stack.reframe());
    Instruction ARRAYLENGTH = new Instruction("ARRAYLENGTH", Codes.ARRAYLENGTH);
    Instruction ASTORE_0 = new Instruction("ASTORE_0", Codes.ASTORE_0, (stack, register) -> register.put(0,
                                                                                                         stack.pop()));
    Instruction ASTORE_1 = new Instruction("ASTORE_1", Codes.ASTORE_1, (stack, register) -> register.put(1,
                                                                                                         stack.pop()));
    Instruction ASTORE_2 = new Instruction("ASTORE_2", Codes.ASTORE_2, (stack, register) -> register.put(2,
                                                                                                         stack.pop()));
    Instruction ASTORE_3 = new Instruction("ASTORE_3", Codes.ASTORE_3, (stack, register) -> register.put(3,
                                                                                                         stack.pop()));
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
    Instruction ATHROW = new Instruction("ATHROW", Codes.ATHROW, (stack, _) -> stack.pop());
    Instruction BALOAD = new Instruction("BALOAD", Codes.BALOAD, (stack, _) -> {
        stack.pop();
        stack.push(stack.pop().asType().componentType());
    });
    Instruction BASTORE = new Instruction("BASTORE", Codes.BASTORE, (stack, _) -> stack.pop(3));
    PushCode BIPUSH = new PushCode("BIPUSH", Codes.BIPUSH);
    Instruction CALOAD = new Instruction("CALOAD", Codes.CALOAD, (stack, _) -> {
        stack.pop2();
        stack.push(Type.INT);
    });
    Instruction CASTORE = new Instruction("CASTORE", Codes.CASTORE, (stack, _) -> stack.pop(3));
    TypeCode CHECKCAST = new TypeCode("CHECKCAST", Codes.CHECKCAST, (hint, stack) -> stack.replace(hint));
    Instruction D2F = new Instruction("D2F", Codes.D2F, StackNotifier.pop2push(Type.FLOAT));
    Instruction D2I = new Instruction("D2I", Codes.D2I, StackNotifier.pop2push(Type.INT));
    Instruction D2L = new Instruction("D2L", Codes.D2L, StackNotifier.replace(Type.LONG));
    Instruction DADD = new Instruction("DADD", Codes.DADD, StackNotifier.POP2);
    Instruction DALOAD = new Instruction("DALOAD", Codes.DALOAD, StackNotifier.pop2push(Type.DOUBLE));
    Instruction DASTORE = new Instruction("DASTORE", Codes.DASTORE, StackNotifier.pop(4));
    Instruction DCMPG = new Instruction("DCMPG", Codes.DCMPG, (stack, _) -> {
        stack.pop(4);
        stack.push(Type.INT);
    });
    Instruction DCMPL = new Instruction("DCMPL", Codes.DCMPL, (stack, _) -> {
        stack.pop(4);
        stack.push(Type.INT);
    });
    Instruction DCONST_0 = new Instruction("DCONST_0", Codes.DCONST_0, StackNotifier.push(Type.DOUBLE));
    Instruction DCONST_1 = new Instruction("DCONST_1", Codes.DCONST_1, StackNotifier.push(Type.DOUBLE));
    Instruction DDIV = new Instruction("DDIV", Codes.DDIV, StackNotifier.POP2);
    Instruction DLOAD_0 = new Instruction("DLOAD_0", Codes.DLOAD_0, StackNotifier.pushVariable(0));
    Instruction DLOAD_1 = new Instruction("DLOAD_1", Codes.DLOAD_1, StackNotifier.pushVariable(1));
    Instruction DLOAD_2 = new Instruction("DLOAD_2", Codes.DLOAD_2, StackNotifier.pushVariable(2));
    Instruction DLOAD_3 = new Instruction("DLOAD_3", Codes.DLOAD_3, StackNotifier.pushVariable(3));
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
    Instruction DMUL = new Instruction("DMUL", Codes.DMUL, StackNotifier.POP2);
    Instruction DNEG = new Instruction("DNEG", Codes.DNEG);
    Instruction DREM = new Instruction("DREM", Codes.DREM, StackNotifier.POP2);
    Instruction DRETURN = new Instruction("DRETURN", Codes.DRETURN, (stack, _) -> stack.reframe());
    Instruction DSTORE_0 = new Instruction("DSTORE_0", Codes.DSTORE_0, new VariableNotifier(0));
    Instruction DSTORE_1 = new Instruction("DSTORE_1", Codes.DSTORE_1, new VariableNotifier(1));
    Instruction DSTORE_2 = new Instruction("DSTORE_2", Codes.DSTORE_2, new VariableNotifier(2));
    Instruction DSTORE_3 = new Instruction("DSTORE_3", Codes.DSTORE_3, new VariableNotifier(3));
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
    Instruction DSUB = new Instruction("DSUB", Codes.DSUB, StackNotifier.POP2);
    Instruction DUP = new Instruction("DUP", Codes.DUP, StackNotifier.DUP);
    Instruction DUP_X1 = new Instruction("DUP_X1", Codes.DUP_X1, StackNotifier.DUP_X1);
    Instruction DUP_X2 = new Instruction("DUP_X2", Codes.DUP_X2, StackNotifier.DUP_X2);
    Instruction DUP2 = new Instruction("DUP2", Codes.DUP2, StackNotifier.DUP2);
    Instruction DUP2_X1 = new Instruction("DUP2_X1", Codes.DUP2_X1, StackNotifier.DUP2_X1);
    Instruction DUP2_X2 = new Instruction("DUP2_X2", Codes.DUP2_X2, StackNotifier.DUP2_X2);
    Instruction F2D = new Instruction("F2D", Codes.F2D, StackNotifier.pop1push(Type.DOUBLE));
    Instruction F2I = new Instruction("F2I", Codes.F2I, StackNotifier.pop1push(Type.INT));
    Instruction F2L = new Instruction("F2L", Codes.F2L, StackNotifier.pop1push(Type.LONG));
    Instruction FADD = new Instruction("FADD", Codes.FADD, StackNotifier.POP);
    Instruction FALOAD = new Instruction("FALOAD", Codes.FALOAD, StackNotifier.pop2push(Type.FLOAT));
    Instruction FASTORE = new Instruction("FASTORE", Codes.FASTORE, StackNotifier.pop(3));
    Instruction FCMPG = new Instruction("FCMPG", Codes.FCMPG, StackNotifier.pop2push(Type.INT));
    Instruction FCMPL = new Instruction("FCMPL", Codes.FCMPL, StackNotifier.pop2push(Type.INT));
    Instruction FCONST_0 = new Instruction("FCONST_0", Codes.FCONST_0, StackNotifier.push(Type.FLOAT));
    Instruction FCONST_1 = new Instruction("FCONST_1", Codes.FCONST_1, StackNotifier.push(Type.FLOAT));
    Instruction FCONST_2 = new Instruction("FCONST_2", Codes.FCONST_2, StackNotifier.push(Type.FLOAT));
    Instruction FDIV = new Instruction("FDIV", Codes.FDIV, StackNotifier.POP);
    Instruction FLOAD_0 = new Instruction("FLOAD_0", Codes.FLOAD_0, StackNotifier.pushVariable(0));
    Instruction FLOAD_1 = new Instruction("FLOAD_1", Codes.FLOAD_1, StackNotifier.pushVariable(1));
    Instruction FLOAD_2 = new Instruction("FLOAD_2", Codes.FLOAD_2, StackNotifier.pushVariable(2));
    Instruction FLOAD_3 = new Instruction("FLOAD_3", Codes.FLOAD_3, StackNotifier.pushVariable(3));
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
    Instruction FMUL = new Instruction("FMUL", Codes.FMUL, StackNotifier.POP);
    Instruction FNEG = new Instruction("FNEG", Codes.FNEG);
    Instruction FREM = new Instruction("FREM", Codes.FREM, StackNotifier.POP);
    Instruction FRETURN = new Instruction("FRETURN", Codes.FRETURN, StackNotifier.POP);
    Instruction FSTORE_0 = new Instruction("FSTORE_0", Codes.FSTORE_0, new VariableNotifier(0));
    Instruction FSTORE_1 = new Instruction("FSTORE_1", Codes.FSTORE_1, new VariableNotifier(1));
    Instruction FSTORE_2 = new Instruction("FSTORE_2", Codes.FSTORE_2, new VariableNotifier(2));
    Instruction FSTORE_3 = new Instruction("FSTORE_3", Codes.FSTORE_3, new VariableNotifier(3));
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
    Instruction FSUB = new Instruction("FSUB", Codes.FSUB, StackNotifier.POP);
    FieldCode GETFIELD = new FieldCode("GETFIELD", Codes.GETFIELD);
    FieldCode GETSTATIC = new FieldCode("GETSTATIC", Codes.GETSTATIC);
    JumpCode GOTO = new JumpCode("GOTO", Codes.GOTO, _ -> {});
    JumpCode GOTO_W = new JumpCode("GOTO_W", Codes.GOTO_W, _ -> {});
    Instruction I2B = new Instruction("I2B", Codes.I2B);
    Instruction I2C = new Instruction("I2C", Codes.I2C);
    Instruction I2D = new Instruction("I2D", Codes.I2D, StackNotifier.pop1push(Type.DOUBLE));
    Instruction I2F = new Instruction("I2F", Codes.I2F, StackNotifier.pop1push(Type.FLOAT));
    Instruction I2L = new Instruction("I2L", Codes.I2L, StackNotifier.pop1push(Type.LONG));
    Instruction I2S = new Instruction("I2S", Codes.I2S);
    Instruction IADD = new Instruction("IADD", Codes.IADD, StackNotifier.POP);
    Instruction IALOAD = new Instruction("IALOAD", Codes.IALOAD, StackNotifier.pop2push(Type.INT));
    Instruction IAND = new Instruction("IAND", Codes.IAND, StackNotifier.POP);
    Instruction IASTORE = new Instruction("IASTORE", Codes.IASTORE, StackNotifier.pop(3));
    Instruction ICONST_M1 = new Instruction("ICONST_M1", Codes.ICONST_M1, StackNotifier.push(Type.INT));
    Instruction ICONST_0 = new Instruction("ICONST_0", Codes.ICONST_0, StackNotifier.push(Type.INT));
    Instruction ICONST_1 = new Instruction("ICONST_1", Codes.ICONST_1, StackNotifier.push(Type.INT));
    Instruction ICONST_2 = new Instruction("ICONST_2", Codes.ICONST_2, StackNotifier.push(Type.INT));
    Instruction ICONST_3 = new Instruction("ICONST_3", Codes.ICONST_3, StackNotifier.push(Type.INT));
    Instruction ICONST_4 = new Instruction("ICONST_4", Codes.ICONST_4, StackNotifier.push(Type.INT));
    Instruction ICONST_5 = new Instruction("ICONST_5", Codes.ICONST_5, StackNotifier.push(Type.INT));
    Instruction IDIV = new Instruction("IDIV", Codes.IDIV, StackNotifier.POP);
    JumpCode IF_ACMPEQ = new JumpCode("IF_ACMPEQ", Codes.IF_ACMPEQ, StackNotifier.POP2);
    JumpCode IF_ACMPNE = new JumpCode("IF_ACMPNE", Codes.IF_ACMPNE, StackNotifier.POP2);
    JumpCode IF_ICMPEQ = new JumpCode("IF_ICMPEQ", Codes.IF_ICMPEQ, StackNotifier.POP2);
    JumpCode IF_ICMPNE = new JumpCode("IF_ICMPNE", Codes.IF_ICMPNE, StackNotifier.POP2);
    JumpCode IF_ICMPLT = new JumpCode("IF_ICMPLT", Codes.IF_ICMPLT, StackNotifier.POP2);
    JumpCode IF_ICMPGE = new JumpCode("IF_ICMPGE", Codes.IF_ICMPGE, StackNotifier.POP2);
    JumpCode IF_ICMPGT = new JumpCode("IF_ICMPGT", Codes.IF_ICMPGT, StackNotifier.POP2);
    JumpCode IF_ICMPLE = new JumpCode("IF_ICMPLE", Codes.IF_ICMPLE, StackNotifier.POP2);
    JumpCode IFEQ = new JumpCode("IFEQ", Codes.IFEQ, StackNotifier.POP);
    JumpCode IFNE = new JumpCode("IFNE", Codes.IFNE, StackNotifier.POP);
    JumpCode IFLT = new JumpCode("IFLT", Codes.IFLT, StackNotifier.POP);
    JumpCode IFGE = new JumpCode("IFGE", Codes.IFGE, StackNotifier.POP);
    JumpCode IFGT = new JumpCode("IFGT", Codes.IFGT, StackNotifier.POP);
    JumpCode IFLE = new JumpCode("IFLE", Codes.IFLE, StackNotifier.POP);
    JumpCode IFNONNULL = new JumpCode("IFNONNULL", Codes.IFNONNULL, StackNotifier.POP);
    JumpCode IFNULL = new JumpCode("IFNULL", Codes.IFNULL, StackNotifier.POP);
    IncrementCode IINC = new IncrementCode("IINC", Codes.IINC);
    Instruction ILOAD_0 = new Instruction("ILOAD_0", Codes.ILOAD_0, StackNotifier.pushVariable(0));
    Instruction ILOAD_1 = new Instruction("ILOAD_1", Codes.ILOAD_1, StackNotifier.pushVariable(1));
    Instruction ILOAD_2 = new Instruction("ILOAD_2", Codes.ILOAD_2, StackNotifier.pushVariable(2));
    Instruction ILOAD_3 = new Instruction("ILOAD_3", Codes.ILOAD_3, StackNotifier.pushVariable(3));
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
    Instruction IMUL = new Instruction("IMUL", Codes.IMUL, StackNotifier.POP);
    Instruction INEG = new Instruction("INEG", Codes.INEG);
    TypeCode INSTANCEOF = new TypeCode("INSTANCEOF", Codes.INSTANCEOF, (_, stack) -> stack.replace(Type.INT));
    InvokeDynamicCode INVOKEDYNAMIC = new InvokeDynamicCode("INVOKEDYNAMIC", Codes.INVOKEDYNAMIC);
    InvokeInterfaceCode INVOKEINTERFACE = new InvokeInterfaceCode("INVOKEINTERFACE", Codes.INVOKEINTERFACE);
    InvokeCode INVOKESPECIAL = new InvokeCode("INVOKESPECIAL", Codes.INVOKESPECIAL);
    InvokeCode INVOKESTATIC = new InvokeCode("INVOKESTATIC", Codes.INVOKESTATIC);
    InvokeCode INVOKEVIRTUAL = new InvokeCode("INVOKEVIRTUAL", Codes.INVOKEVIRTUAL);
    Instruction IOR = new Instruction("IOR", Codes.IOR, StackNotifier.POP);
    Instruction IREM = new Instruction("IREM", Codes.IREM, StackNotifier.POP);
    Instruction IRETURN = new Instruction("IRETURN", Codes.IRETURN, StackNotifier.POP);
    Instruction ISHL = new Instruction("ISHL", Codes.ISHL);
    Instruction ISHR = new Instruction("ISHR", Codes.ISHR);
    Instruction ISTORE_0 = new Instruction("ISTORE_0", Codes.ISTORE_0, new VariableNotifier(0));
    Instruction ISTORE_1 = new Instruction("ISTORE_1", Codes.ISTORE_1, new VariableNotifier(1));
    Instruction ISTORE_2 = new Instruction("ISTORE_2", Codes.ISTORE_2, new VariableNotifier(2));
    Instruction ISTORE_3 = new Instruction("ISTORE_3", Codes.ISTORE_3, new VariableNotifier(3));
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
    Instruction ISUB = new Instruction("ISUB", Codes.ISUB, StackNotifier.POP);
    Instruction IUSHR = new Instruction("IUSHR", Codes.IUSHR);
    Instruction IXOR = new Instruction("IXOR", Codes.IXOR, StackNotifier.POP);
    UncheckedCode JSR = new UncheckedCode("JSR", Codes.JSR);
    UncheckedCode JSR_W = new UncheckedCode("JSR_W", Codes.JSR_W);
    Instruction L2D = new Instruction("L2D", Codes.L2D, StackNotifier.pop2push(Type.DOUBLE));
    Instruction L2F = new Instruction("L2F", Codes.L2F, StackNotifier.pop2push(Type.FLOAT));
    Instruction L2I = new Instruction("L2I", Codes.L2I, StackNotifier.pop2push(Type.INT));
    Instruction LADD = new Instruction("LADD", Codes.LADD, StackNotifier.POP2);
    Instruction LALOAD = new Instruction("LALOAD", Codes.LALOAD, StackNotifier.pop2push(Type.LONG));
    Instruction LAND = new Instruction("LAND", Codes.LAND, (stack, _) -> {
        stack.pop(4);
        stack.push(Type.INT);
    });
    Instruction LASTORE = new Instruction("LASTORE", Codes.LASTORE, StackNotifier.pop(4));
    Instruction LCMP = new Instruction("LCMP", Codes.LCMP, (stack, _) -> {
        stack.pop(4);
        stack.push(Type.INT);
    });
    Instruction LCONST_0 = new Instruction("LCONST_0", Codes.LCONST_0, StackNotifier.push(Type.LONG));
    Instruction LCONST_1 = new Instruction("LCONST_1", Codes.LCONST_1, StackNotifier.push(Type.LONG));
    LoadConstantCode LDC = new LoadConstantCode("LDC", Codes.LDC);
    UncheckedCode LDC_W = new UncheckedCode("LDC_W", Codes.LDC_W);
    UncheckedCode LDC2_W = new UncheckedCode("LDC2_W", Codes.LDC2_W);
    Instruction LDIV = new Instruction("LDIV", Codes.LDIV);
    Instruction LLOAD_0 = new Instruction("LLOAD_0", Codes.LLOAD_0, StackNotifier.pushVariable(0));
    Instruction LLOAD_1 = new Instruction("LLOAD_1", Codes.LLOAD_1, StackNotifier.pushVariable(1));
    Instruction LLOAD_2 = new Instruction("LLOAD_2", Codes.LLOAD_2, StackNotifier.pushVariable(2));
    Instruction LLOAD_3 = new Instruction("LLOAD_3", Codes.LLOAD_3, StackNotifier.pushVariable(3));
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
    Instruction LRETURN = new Instruction("LRETURN", Codes.LRETURN, (stack, _) -> stack.reframe());
    Instruction LSHL = new Instruction("LSHL", Codes.LSHL);
    Instruction LSHR = new Instruction("LSHR", Codes.LSHR);
    Instruction LSTORE_0 = new Instruction("LSTORE_0", Codes.LSTORE_0, new VariableNotifier(0));
    Instruction LSTORE_1 = new Instruction("LSTORE_1", Codes.LSTORE_1, new VariableNotifier(1));
    Instruction LSTORE_2 = new Instruction("LSTORE_2", Codes.LSTORE_2, new VariableNotifier(2));
    Instruction LSTORE_3 = new Instruction("LSTORE_3", Codes.LSTORE_3, new VariableNotifier(3));
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
    TypeCode NEW = new TypeCode("NEW", Codes.NEW, null);
    TypeCode.Array NEWARRAY = new TypeCode.Array("NEWARRAY", Codes.NEWARRAY);
    Instruction NOP = new Instruction("NOP", Codes.NOP);
    Instruction POP = new Instruction("POP", Codes.POP, StackNotifier.POP);
    Instruction POP2 = new Instruction("POP2", Codes.POP2, StackNotifier.POP2);
    FieldCode PUTFIELD = new FieldCode("PUTFIELD", Codes.PUTFIELD);
    FieldCode PUTSTATIC = new FieldCode("PUTSTATIC", Codes.PUTSTATIC);
    VariableCode RET = new VariableCode("RET", Codes.RET) {};
    Instruction RETURN = new Instruction("RETURN", Codes.RETURN, (stack, _) -> stack.reframe());
    Instruction SALOAD = new Instruction("SALOAD", Codes.SALOAD, StackNotifier.pop2push(Type.INT));
    Instruction SASTORE = new Instruction("SASTORE", Codes.SASTORE, StackNotifier.pop(3));
    PushCode SIPUSH = new PushCode("SIPUSH", Codes.SIPUSH);
    Instruction SWAP = new Instruction("SWAP", Codes.SWAP, StackNotifier.SWAP);
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
