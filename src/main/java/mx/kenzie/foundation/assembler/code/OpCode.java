package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.U1;
import mx.kenzie.foundation.assembler.U2;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.constant.ConstantPoolInfo;
import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

/**
 * Operation code.
 * An instruction in the virtual machine.
 */
public interface OpCode {

    /**
     * 1: aaload
     */
    Instruction AALOAD = new Instruction("AALOAD", Codes.AALOAD);
    /**
     * 1: aastore
     */
    Instruction AASTORE = new Instruction("AASTORE", Codes.AASTORE);
    /**
     * 1: aconst_null
     */
    Instruction ACONST_NULL = new Instruction("ACONST_NULL", Codes.ACONST_NULL);
    /**
     * 1: <opcode>
     */
    Instruction ALOAD_0 = new Instruction("ALOAD_0", Codes.ALOAD_0);
    /**
     * 1: <opcode>
     */
    Instruction ALOAD_1 = new Instruction("ALOAD_1", Codes.ALOAD_1);
    /**
     * 1: <opcode>
     */
    Instruction ALOAD_2 = new Instruction("ALOAD_2", Codes.ALOAD_2);
    /**
     * 1: <opcode>
     */
    Instruction ALOAD_3 = new Instruction("ALOAD_3", Codes.ALOAD_3);
    /**
     * 2: aload, index
     */
    VariableInstruction ALOAD = new VariableInstruction("ALOAD", Codes.ALOAD) {
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
    /**
     * 3: anewarray, indexbyte1, indexbyte2
     */
    UncheckedCode ANEWARRAY = new UncheckedCode("ANEWARRAY", Codes.ANEWARRAY);
    /**
     * 1: areturn
     */
    Instruction ARETURN = new Instruction("ARETURN", Codes.ARETURN);
    /**
     * 1: arraylength
     */
    Instruction ARRAYLENGTH = new Instruction("ARRAYLENGTH", Codes.ARRAYLENGTH);
    /**
     * 1: <opcode>
     */
    Instruction ASTORE_0 = new Instruction("ASTORE_0", Codes.ASTORE_0);
    /**
     * 1: <opcode>
     */
    Instruction ASTORE_1 = new Instruction("ASTORE_1", Codes.ASTORE_1);
    /**
     * 1: <opcode>
     */
    Instruction ASTORE_2 = new Instruction("ASTORE_2", Codes.ASTORE_2);
    /**
     * 1: <opcode>
     */
    Instruction ASTORE_3 = new Instruction("ASTORE_3", Codes.ASTORE_3);
    /**
     * 2: astore, index
     */
    VariableInstruction ASTORE = new VariableInstruction("ASTORE", Codes.ASTORE) {
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
    /**
     * 1: athrow
     */
    Instruction ATHROW = new Instruction("ATHROW", Codes.ATHROW);
    /**
     * 1: baload
     */
    Instruction BALOAD = new Instruction("BALOAD", Codes.BALOAD);
    /**
     * 1: bastore
     */
    Instruction BASTORE = new Instruction("BASTORE", Codes.BASTORE);
    /**
     * 2: bipush, byte
     */
    UncheckedCode BIPUSH = new UncheckedCode("BIPUSH", Codes.BIPUSH);
    /**
     * 1: caload
     */
    Instruction CALOAD = new Instruction("CALOAD", Codes.CALOAD);
    /**
     * 1: castore
     */
    Instruction CASTORE = new Instruction("CASTORE", Codes.CASTORE);
    /**
     * 3: checkcast, indexbyte1, indexbyte2
     */
    UncheckedCode CHECKCAST = new UncheckedCode("CHECKCAST", Codes.CHECKCAST);
    /**
     * 1: d2f
     */
    Instruction D2F = new Instruction("D2F", Codes.D2F);
    /**
     * 1: d2i
     */
    Instruction D2I = new Instruction("D2I", Codes.D2I);
    /**
     * 1: d2l
     */
    Instruction D2L = new Instruction("D2L", Codes.D2L);
    /**
     * 1: dadd
     */
    Instruction DADD = new Instruction("DADD", Codes.DADD);
    /**
     * 1: daload
     */
    Instruction DALOAD = new Instruction("DALOAD", Codes.DALOAD);
    /**
     * 1: dastore
     */
    Instruction DASTORE = new Instruction("DASTORE", Codes.DASTORE);
    /**
     * 1: <opcode>
     */
    Instruction DCMPG = new Instruction("DCMPG", Codes.DCMPG);
    /**
     * 1: <opcode>
     */
    Instruction DCMPL = new Instruction("DCMPL", Codes.DCMPL);
    /**
     * 1: <opcode>
     */
    Instruction DCONST_0 = new Instruction("DCONST_0", Codes.DCONST_0);
    /**
     * 1: <opcode>
     */
    Instruction DCONST_1 = new Instruction("DCONST_1", Codes.DCONST_1);
    /**
     * 1: ddiv
     */
    Instruction DDIV = new Instruction("DDIV", Codes.DDIV);
    /**
     * 1: <opcode>
     */
    Instruction DLOAD_0 = new Instruction("DLOAD_0", Codes.DLOAD_0);
    /**
     * 1: <opcode>
     */
    Instruction DLOAD_1 = new Instruction("DLOAD_1", Codes.DLOAD_1);
    /**
     * 1: <opcode>
     */
    Instruction DLOAD_2 = new Instruction("DLOAD_2", Codes.DLOAD_2);
    /**
     * 1: <opcode>
     */
    Instruction DLOAD_3 = new Instruction("DLOAD_3", Codes.DLOAD_3);
    /**
     * 2: dload, index
     */
    VariableInstruction DLOAD = new VariableInstruction("DLOAD", Codes.DLOAD) {
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
    /**
     * 1: dmul
     */
    Instruction DMUL = new Instruction("DMUL", Codes.DMUL);
    /**
     * 1: dneg
     */
    Instruction DNEG = new Instruction("DNEG", Codes.DNEG);
    /**
     * 1: drem
     */
    Instruction DREM = new Instruction("DREM", Codes.DREM);
    /**
     * 1: dreturn
     */
    Instruction DRETURN = new Instruction("DRETURN", Codes.DRETURN);
    /**
     * 1: <opcode>
     */
    Instruction DSTORE_0 = new Instruction("DSTORE_0", Codes.DSTORE_0);
    /**
     * 1: <opcode>
     */
    Instruction DSTORE_1 = new Instruction("DSTORE_1", Codes.DSTORE_1);
    /**
     * 1: <opcode>
     */
    Instruction DSTORE_2 = new Instruction("DSTORE_2", Codes.DSTORE_2);
    /**
     * 1: <opcode>
     */
    Instruction DSTORE_3 = new Instruction("DSTORE_3", Codes.DSTORE_3);
    /**
     * 2: dstore, index
     */
    VariableInstruction DSTORE = new VariableInstruction("DSTORE", Codes.DSTORE) {
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
    /**
     * 1: dsub
     */
    Instruction DSUB = new Instruction("DSUB", Codes.DSUB);
    /**
     * 1: dup
     */
    Instruction DUP = new Instruction("DUP", Codes.DUP);
    /**
     * 1: dup_x1
     */
    Instruction DUP_X1 = new Instruction("DUP_X1", Codes.DUP_X1);
    /**
     * 1: dup_x2
     */
    Instruction DUP_X2 = new Instruction("DUP_X2", Codes.DUP_X2);
    /**
     * 1: dup2
     */
    Instruction DUP2 = new Instruction("DUP2", Codes.DUP2);
    /**
     * 1: dup2_x1
     */
    Instruction DUP2_X1 = new Instruction("DUP2_X1", Codes.DUP2_X1);
    /**
     * 1: dup2_x2
     */
    Instruction DUP2_X2 = new Instruction("DUP2_X2", Codes.DUP2_X2);
    /**
     * 1: f2d
     */
    Instruction F2D = new Instruction("F2D", Codes.F2D);
    /**
     * 1: f2i
     */
    Instruction F2I = new Instruction("F2I", Codes.F2I);
    /**
     * 1: f2l
     */
    Instruction F2L = new Instruction("F2L", Codes.F2L);
    /**
     * 1: fadd
     */
    Instruction FADD = new Instruction("FADD", Codes.FADD);
    /**
     * 1: faload
     */
    Instruction FALOAD = new Instruction("FALOAD", Codes.FALOAD);
    /**
     * 1: fastore
     */
    Instruction FASTORE = new Instruction("FASTORE", Codes.FASTORE);
    /**
     * 1: <opcode>
     */
    Instruction FCMPG = new Instruction("FCMPG", Codes.FCMPG);
    /**
     * 1: <opcode>
     */
    Instruction FCMPL = new Instruction("FCMPL", Codes.FCMPL);
    /**
     * 1: <opcode>
     */
    Instruction FCONST_0 = new Instruction("FCONST_0", Codes.FCONST_0);
    /**
     * 1: <opcode>
     */
    Instruction FCONST_1 = new Instruction("FCONST_1", Codes.FCONST_1);
    /**
     * 1: <opcode>
     */
    Instruction FCONST_2 = new Instruction("FCONST_2", Codes.FCONST_2);
    /**
     * 1: fdiv
     */
    Instruction FDIV = new Instruction("FDIV", Codes.FDIV);
    /**
     * 1: <opcode>
     */
    Instruction FLOAD_0 = new Instruction("FLOAD_0", Codes.FLOAD_0);
    /**
     * 1: <opcode>
     */
    Instruction FLOAD_1 = new Instruction("FLOAD_1", Codes.FLOAD_1);
    /**
     * 1: <opcode>
     */
    Instruction FLOAD_2 = new Instruction("FLOAD_2", Codes.FLOAD_2);
    /**
     * 1: <opcode>
     */
    Instruction FLOAD_3 = new Instruction("FLOAD_3", Codes.FLOAD_3);
    /**
     * 2: fload, index
     */
    VariableInstruction FLOAD = new VariableInstruction("FLOAD", Codes.FLOAD) {
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
    /**
     * 1: fmul
     */
    Instruction FMUL = new Instruction("FMUL", Codes.FMUL);
    /**
     * 1: fneg
     */
    Instruction FNEG = new Instruction("FNEG", Codes.FNEG);
    /**
     * 1: frem
     */
    Instruction FREM = new Instruction("FREM", Codes.FREM);
    /**
     * 1: freturn
     */
    Instruction FRETURN = new Instruction("FRETURN", Codes.FRETURN);
    /**
     * 1: <opcode>
     */
    Instruction FSTORE_0 = new Instruction("FSTORE_0", Codes.FSTORE_0);
    /**
     * 1: <opcode>
     */
    Instruction FSTORE_1 = new Instruction("FSTORE_1", Codes.FSTORE_1);
    /**
     * 1: <opcode>
     */
    Instruction FSTORE_2 = new Instruction("FSTORE_2", Codes.FSTORE_2);
    /**
     * 1: <opcode>
     */
    Instruction FSTORE_3 = new Instruction("FSTORE_3", Codes.FSTORE_3);
    /**
     * 2: fstore, index
     */
    VariableInstruction FSTORE = new VariableInstruction("FSTORE", Codes.FSTORE) {
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
    /**
     * 1: fsub
     */
    Instruction FSUB = new Instruction("FSUB", Codes.FSUB);
    /**
     * 3: getfield, indexbyte1, indexbyte2
     */
    UncheckedCode GETFIELD = new UncheckedCode("GETFIELD", Codes.GETFIELD);
    /**
     * 3: getstatic, indexbyte1, indexbyte2
     */
    UncheckedCode GETSTATIC = new UncheckedCode("GETSTATIC", Codes.GETSTATIC);
    /**
     * 3: goto, branchbyte1, branchbyte2
     */
    UncheckedCode GOTO = new UncheckedCode("GOTO", Codes.GOTO);
    /**
     * 5: goto_w, branchbyte1, branchbyte2, branchbyte3, branchbyte4
     */
    UncheckedCode GOTO_W = new UncheckedCode("GOTO_W", Codes.GOTO_W);
    /**
     * 1: i2b
     */
    Instruction I2B = new Instruction("I2B", Codes.I2B);
    /**
     * 1: i2c
     */
    Instruction I2C = new Instruction("I2C", Codes.I2C);
    /**
     * 1: i2d
     */
    Instruction I2D = new Instruction("I2D", Codes.I2D);
    /**
     * 1: i2f
     */
    Instruction I2F = new Instruction("I2F", Codes.I2F);
    /**
     * 1: i2l
     */
    Instruction I2L = new Instruction("I2L", Codes.I2L);
    /**
     * 1: i2s
     */
    Instruction I2S = new Instruction("I2S", Codes.I2S);
    /**
     * 1: iadd
     */
    Instruction IADD = new Instruction("IADD", Codes.IADD);
    /**
     * 1: iaload
     */
    Instruction IALOAD = new Instruction("IALOAD", Codes.IALOAD);
    /**
     * 1: iand
     */
    Instruction IAND = new Instruction("IAND", Codes.IAND);
    /**
     * 1: iastore
     */
    Instruction IASTORE = new Instruction("IASTORE", Codes.IASTORE);
    /**
     * 1: <opcode>
     */
    Instruction ICONST_M1 = new Instruction("ICONST_M1", Codes.ICONST_M1);
    /**
     * 1: <opcode>
     */
    Instruction ICONST_0 = new Instruction("ICONST_0", Codes.ICONST_0);
    /**
     * 1: <opcode>
     */
    Instruction ICONST_1 = new Instruction("ICONST_1", Codes.ICONST_1);
    /**
     * 1: <opcode>
     */
    Instruction ICONST_2 = new Instruction("ICONST_2", Codes.ICONST_2);
    /**
     * 1: <opcode>
     */
    Instruction ICONST_3 = new Instruction("ICONST_3", Codes.ICONST_3);
    /**
     * 1: <opcode>
     */
    Instruction ICONST_4 = new Instruction("ICONST_4", Codes.ICONST_4);
    /**
     * 1: <opcode>
     */
    Instruction ICONST_5 = new Instruction("ICONST_5", Codes.ICONST_5);
    /**
     * 1: idiv
     */
    Instruction IDIV = new Instruction("IDIV", Codes.IDIV);
    /**
     * 1: <opcode>
     */
    Instruction IF_ACMPEQ = new Instruction("IF_ACMPEQ", Codes.IF_ACMPEQ);
    /**
     * 1: <opcode>
     */
    Instruction IF_ACMPNE = new Instruction("IF_ACMPNE", Codes.IF_ACMPNE);
    /**
     * 1: <opcode>
     */
    Instruction IF_ICMPEQ = new Instruction("IF_ICMPEQ", Codes.IF_ICMPEQ);
    /**
     * 1: <opcode>
     */
    Instruction IF_ICMPNE = new Instruction("IF_ICMPNE", Codes.IF_ICMPNE);
    /**
     * 1: <opcode>
     */
    Instruction IF_ICMPLT = new Instruction("IF_ICMPLT", Codes.IF_ICMPLT);
    /**
     * 1: <opcode>
     */
    Instruction IF_ICMPGE = new Instruction("IF_ICMPGE", Codes.IF_ICMPGE);
    /**
     * 1: <opcode>
     */
    Instruction IF_ICMPGT = new Instruction("IF_ICMPGT", Codes.IF_ICMPGT);
    /**
     * 1: <opcode>
     */
    Instruction IF_ICMPLE = new Instruction("IF_ICMPLE", Codes.IF_ICMPLE);
    /**
     * 1: <opcode>
     */
    Instruction IFEQ = new Instruction("IFEQ", Codes.IFEQ);
    /**
     * 1: <opcode>
     */
    Instruction IFNE = new Instruction("IFNE", Codes.IFNE);
    /**
     * 1: <opcode>
     */
    Instruction IFLT = new Instruction("IFLT", Codes.IFLT);
    /**
     * 1: <opcode>
     */
    Instruction IFGE = new Instruction("IFGE", Codes.IFGE);
    /**
     * 1: <opcode>
     */
    Instruction IFGT = new Instruction("IFGT", Codes.IFGT);
    /**
     * 1: <opcode>
     */
    Instruction IFLE = new Instruction("IFLE", Codes.IFLE);
    /**
     * 3: ifnonnull, branchbyte1, branchbyte2
     */
    UncheckedCode IFNONNULL = new UncheckedCode("IFNONNULL", Codes.IFNONNULL);
    /**
     * 3: ifnull, branchbyte1, branchbyte2
     */
    UncheckedCode IFNULL = new UncheckedCode("IFNULL", Codes.IFNULL);
    /**
     * 3: iinc, index, const
     */
    Increment IINC = new Increment("IINC", Codes.IINC);
    /**
     * 1: <opcode>
     */
    Instruction ILOAD_0 = new Instruction("ILOAD_0", Codes.ILOAD_0);
    /**
     * 1: <opcode>
     */
    Instruction ILOAD_1 = new Instruction("ILOAD_1", Codes.ILOAD_1);
    /**
     * 1: <opcode>
     */
    Instruction ILOAD_2 = new Instruction("ILOAD_2", Codes.ILOAD_2);
    /**
     * 1: <opcode>
     */
    Instruction ILOAD_3 = new Instruction("ILOAD_3", Codes.ILOAD_3);
    /**
     * 2: iload, index
     */
    VariableInstruction ILOAD = new VariableInstruction("ILOAD", Codes.ILOAD) {
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
    /**
     * 1: imul
     */
    Instruction IMUL = new Instruction("IMUL", Codes.IMUL);
    /**
     * 1: ineg
     */
    Instruction INEG = new Instruction("INEG", Codes.INEG);
    /**
     * 3: instanceof, indexbyte1, indexbyte2
     */
    UncheckedCode INSTANCEOF = new UncheckedCode("INSTANCEOF", Codes.INSTANCEOF);
    /**
     * 5: invokedynamic, indexbyte1, indexbyte2, 0, 0
     */
    UncheckedCode INVOKEDYNAMIC = new UncheckedCode("INVOKEDYNAMIC", Codes.INVOKEDYNAMIC);
    /**
     * 5: invokeinterface, indexbyte1, indexbyte2, count, 0
     */
    UncheckedCode INVOKEINTERFACE = new UncheckedCode("INVOKEINTERFACE", Codes.INVOKEINTERFACE);
    /**
     * 3: invokespecial, indexbyte1, indexbyte2
     */
    UncheckedCode INVOKESPECIAL = new UncheckedCode("INVOKESPECIAL", Codes.INVOKESPECIAL);
    /**
     * 3: invokestatic, indexbyte1, indexbyte2
     */
    UncheckedCode INVOKESTATIC = new UncheckedCode("INVOKESTATIC", Codes.INVOKESTATIC);
    /**
     * 3: invokevirtual, indexbyte1, indexbyte2
     */
    UncheckedCode INVOKEVIRTUAL = new UncheckedCode("INVOKEVIRTUAL", Codes.INVOKEVIRTUAL);
    /**
     * 1: ior
     */
    Instruction IOR = new Instruction("IOR", Codes.IOR);
    /**
     * 1: irem
     */
    Instruction IREM = new Instruction("IREM", Codes.IREM);
    /**
     * 1: ireturn
     */
    Instruction IRETURN = new Instruction("IRETURN", Codes.IRETURN);
    /**
     * 1: ishl
     */
    Instruction ISHL = new Instruction("ISHL", Codes.ISHL);
    /**
     * 1: ishr
     */
    Instruction ISHR = new Instruction("ISHR", Codes.ISHR);
    /**
     * 1: <opcode>
     */
    Instruction ISTORE_0 = new Instruction("ISTORE_0", Codes.ISTORE_0);
    /**
     * 1: <opcode>
     */
    Instruction ISTORE_1 = new Instruction("ISTORE_1", Codes.ISTORE_1);
    /**
     * 1: <opcode>
     */
    Instruction ISTORE_2 = new Instruction("ISTORE_2", Codes.ISTORE_2);
    /**
     * 1: <opcode>
     */
    Instruction ISTORE_3 = new Instruction("ISTORE_3", Codes.ISTORE_3);
    /**
     * 2: istore, index
     */
    VariableInstruction ISTORE = new VariableInstruction("ISTORE", Codes.ISTORE) {
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
    /**
     * 1: isub
     */
    Instruction ISUB = new Instruction("ISUB", Codes.ISUB);
    /**
     * 1: iushr
     */
    Instruction IUSHR = new Instruction("IUSHR", Codes.IUSHR);
    /**
     * 1: ixor
     */
    Instruction IXOR = new Instruction("IXOR", Codes.IXOR);
    /**
     * 3: jsr, branchbyte1, branchbyte2
     */
    UncheckedCode JSR = new UncheckedCode("JSR", Codes.JSR);
    /**
     * 5: jsr_w, branchbyte1, branchbyte2, branchbyte3, branchbyte4
     */
    UncheckedCode JSR_W = new UncheckedCode("JSR_W", Codes.JSR_W);
    /**
     * 1: l2d
     */
    Instruction L2D = new Instruction("L2D", Codes.L2D);
    /**
     * 1: l2f
     */
    Instruction L2F = new Instruction("L2F", Codes.L2F);
    /**
     * 1: l2i
     */
    Instruction L2I = new Instruction("L2I", Codes.L2I);
    /**
     * 1: ladd
     */
    Instruction LADD = new Instruction("LADD", Codes.LADD);
    /**
     * 1: laload
     */
    Instruction LALOAD = new Instruction("LALOAD", Codes.LALOAD);
    /**
     * 1: land
     */
    Instruction LAND = new Instruction("LAND", Codes.LAND);
    /**
     * 1: lastore
     */
    Instruction LASTORE = new Instruction("LASTORE", Codes.LASTORE);
    /**
     * 1: lcmp
     */
    Instruction LCMP = new Instruction("LCMP", Codes.LCMP);
    /**
     * 1: <opcode>
     */
    Instruction LCONST_0 = new Instruction("LCONST_0", Codes.LCONST_0);
    /**
     * 1: <opcode>
     */
    Instruction LCONST_1 = new Instruction("LCONST_1", Codes.LCONST_1);
    /**
     * 2: ldc, index
     */
    LoadConstant LDC = new LoadConstant("LDC", Codes.LDC);
    /**
     * 3: ldc_w, indexbyte1, indexbyte2
     */
    UncheckedCode LDC_W = new UncheckedCode("LDC_W", Codes.LDC_W);
    /**
     * 3: ldc2_w, indexbyte1, indexbyte2
     */
    UncheckedCode LDC2_W = new UncheckedCode("LDC2_W", Codes.LDC2_W);
    /**
     * 1: ldiv
     */
    Instruction LDIV = new Instruction("LDIV", Codes.LDIV);
    /**
     * 1: <opcode>
     */
    Instruction LLOAD_0 = new Instruction("LLOAD_0", Codes.LLOAD_0);
    /**
     * 1: <opcode>
     */
    Instruction LLOAD_1 = new Instruction("LLOAD_1", Codes.LLOAD_1);
    /**
     * 1: <opcode>
     */
    Instruction LLOAD_2 = new Instruction("LLOAD_2", Codes.LLOAD_2);
    /**
     * 1: <opcode>
     */
    Instruction LLOAD_3 = new Instruction("LLOAD_3", Codes.LLOAD_3);
    /**
     * 2: lload, index
     */
    VariableInstruction LLOAD = new VariableInstruction("LLOAD", Codes.LLOAD) {
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
    /**
     * 1: lmul
     */
    Instruction LMUL = new Instruction("LMUL", Codes.LMUL);
    /**
     * 1: lneg
     */
    Instruction LNEG = new Instruction("LNEG", Codes.LNEG);
    /**
     * 11: lookupswitch, <0-3 byte pad>, defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4, npairs1, npairs2,
     * npairs3, npairs4, match-offset pairs...
     */
    UncheckedCode LOOKUPSWITCH = new UncheckedCode("LOOKUPSWITCH", Codes.LOOKUPSWITCH);
    /**
     * 1: lor
     */
    Instruction LOR = new Instruction("LOR", Codes.LOR);
    /**
     * 1: lrem
     */
    Instruction LREM = new Instruction("LREM", Codes.LREM);
    /**
     * 1: lreturn
     */
    Instruction LRETURN = new Instruction("LRETURN", Codes.LRETURN);
    /**
     * 1: lshl
     */
    Instruction LSHL = new Instruction("LSHL", Codes.LSHL);
    /**
     * 1: lshr
     */
    Instruction LSHR = new Instruction("LSHR", Codes.LSHR);
    /**
     * 1: <opcode>
     */
    Instruction LSTORE_0 = new Instruction("LSTORE_0", Codes.LSTORE_0);
    /**
     * 1: <opcode>
     */
    Instruction LSTORE_1 = new Instruction("LSTORE_1", Codes.LSTORE_1);
    /**
     * 1: <opcode>
     */
    Instruction LSTORE_2 = new Instruction("LSTORE_2", Codes.LSTORE_2);
    /**
     * 1: <opcode>
     */
    Instruction LSTORE_3 = new Instruction("LSTORE_3", Codes.LSTORE_3);
    /**
     * 2: lstore, index
     */
    VariableInstruction LSTORE = new VariableInstruction("LSTORE", Codes.LSTORE) {
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
    /**
     * 1: lsub
     */
    Instruction LSUB = new Instruction("LSUB", Codes.LSUB);
    /**
     * 1: lushr
     */
    Instruction LUSHR = new Instruction("LUSHR", Codes.LUSHR);
    /**
     * 1: lxor
     */
    Instruction LXOR = new Instruction("LXOR", Codes.LXOR);
    /**
     * 1: monitorenter
     */
    Instruction MONITORENTER = new Instruction("MONITORENTER", Codes.MONITORENTER);
    /**
     * 1: monitorexit
     */
    Instruction MONITOREXIT = new Instruction("MONITOREXIT", Codes.MONITOREXIT);
    /**
     * 4: multianewarray, indexbyte1, indexbyte2, dimensions
     */
    UncheckedCode MULTIANEWARRAY = new UncheckedCode("MULTIANEWARRAY", Codes.MULTIANEWARRAY);
    /**
     * 3: new, indexbyte1, indexbyte2
     */
    UncheckedCode NEW = new UncheckedCode("NEW", Codes.NEW);
    /**
     * 2: newarray, atype
     */
    UncheckedCode NEWARRAY = new UncheckedCode("NEWARRAY", Codes.NEWARRAY);
    /**
     * 1: nop
     */
    Instruction NOP = new Instruction("NOP", Codes.NOP);
    /**
     * 1: pop
     */
    Instruction POP = new Instruction("POP", Codes.POP);
    /**
     * 1: pop2
     */
    Instruction POP2 = new Instruction("POP2", Codes.POP2);
    /**
     * 3: putfield, indexbyte1, indexbyte2
     */
    UncheckedCode PUTFIELD = new UncheckedCode("PUTFIELD", Codes.PUTFIELD);
    /**
     * 3: putstatic, indexbyte1, indexbyte2
     */
    UncheckedCode PUTSTATIC = new UncheckedCode("PUTSTATIC", Codes.PUTSTATIC);
    /**
     * 2: ret, index
     */
    VariableInstruction RET = new VariableInstruction("RET", Codes.RET) {};
    /**
     * 1: return
     */
    Instruction RETURN = new Instruction("RETURN", Codes.RETURN);
    /**
     * 1: saload
     */
    Instruction SALOAD = new Instruction("SALOAD", Codes.SALOAD);
    /**
     * 1: sastore
     */
    Instruction SASTORE = new Instruction("SASTORE", Codes.SASTORE);
    /**
     * 3: sipush, byte1, byte2
     */
    UncheckedCode SIPUSH = new UncheckedCode("SIPUSH", Codes.SIPUSH);
    /**
     * 1: swap
     */
    Instruction SWAP = new Instruction("SWAP", Codes.SWAP);
    /**
     * 15: tableswitch, <0-3 byte pad>, defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4, lowbyte1, lowbyte2,
     * lowbyte3, lowbyte4, highbyte1, highbyte2, highbyte3, highbyte4, jump offsets...
     */
    UncheckedCode TABLESWITCH = new UncheckedCode("TABLESWITCH", Codes.TABLESWITCH);
    /**
     * Format 1: wide, <opcode>, indexbyte1, indexbyte2, where <opcode> is one of iload, fload, aload, lload,
     * dload, istore, fstore, astore, lstore, dstore, or ret
     * Format 2: wide, iinc, indexbyte1, indexbyte2,
     * constbyte1, constbyte2
     */
    Wide WIDE = new Wide("WIDE", Codes.WIDE);

    String mnemonic();

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

    /**
     * A simple, single-byte instruction, containing only its operation code.
     * Since no additional parameters are needed for this instruction it may be used as-is.
     *
     * @param mnemonic The operation code's reference name.
     * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
     *                 caution.
     */
    record Instruction(String mnemonic, byte code) implements OpCode, CodeElement, RecordConstant {

        public Instruction(String mnemonic, int code) {
            this(mnemonic, (byte) code);
        }

        @Override
        public int length() {
            return 1;
        }

        @Override
        public byte[] binary() {
            return new byte[] {code};
        }

        @Override
        public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
            stream.write(code);
        }

        @Override
        public String toString() {
            return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
        }

    }

    /**
     * An instruction for loading a constant value.
     *
     * @param mnemonic The operation code's reference name.
     * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
     *                 caution.
     */
    record LoadConstant(String mnemonic, byte code) implements OpCode {

        //<editor-fold desc="LDC" defaultstate="collapsed">
        @Override
        public int length() {
            return 1;
        }

        public UnboundedElement value(Constable value) {
            return switch (value) {
                case null -> ACONST_NULL;
                case Long j -> storage -> new WideType(storage.constant(ConstantPoolInfo.LONG, j));
                case Double d -> storage -> new WideType(storage.constant(ConstantPoolInfo.DOUBLE, d));
                default -> storage -> new NarrowType(storage.constant(value));
            };
        }

        @Override
        public String toString() {
            return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
        }

        private record NarrowType(PoolReference reference) implements CodeElement, RecordConstant {

            @Override
            public void write(OutputStream stream) throws IOException {
                final short index = (short) reference.index();
                final boolean wide = index > 255;
                if (wide) {
                    stream.write(Codes.LDC_W);
                    stream.write((index >>> 8));
                    stream.write(index);
                } else {
                    stream.write(Codes.LDC);
                    stream.write((byte) index);
                }
            }

            @Override
            public void notify(CodeBuilder builder) {
                builder.notifyStack(1);
            }

            @Override
            public byte code() {
                return (short) reference.index() > 255 ? Codes.LDC_W : Codes.LDC;
            }

            @Override
            public int length() {
                return reference.index() > 255 ? 3 : 2;
            }

        }

        private record WideType(PoolReference reference) implements CodeElement, RecordConstant {

            @Override
            public void write(OutputStream stream) throws IOException {
                stream.write(Codes.LDC2_W);
                this.reference.write(stream);
            }

            @Override
            public void notify(CodeBuilder builder) {
                builder.notifyStack(2);
            }

            @Override
            public byte code() {
                return Codes.LDC2_W;
            }

            @Override
            public int length() {
                return 3;
            }

        }
        //</editor-fold>

    }

    /**
     * An instruction for incrementing the numeric value in a variable slot.
     *
     * @param mnemonic The operation code's reference name.
     * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
     *                 caution.
     */
    record Increment(String mnemonic, byte code) implements OpCode {

        //<editor-fold desc="IINC" defaultstate="collapsed">
        @Override
        public int length() {
            return 3;
        }

        public UnboundedElement var(int slot, int increment) {
            final boolean wide = slot > 255 || increment > Byte.MAX_VALUE || increment < Byte.MIN_VALUE;
            if (wide)
                return CodeElement.incrementStack(CodeElement.wide(CodeElement.fixed(code, (byte) (slot >>> 8),
                    (byte) (slot), (byte) (increment >> 8), (byte) (increment))), 0);
            return CodeElement.fixed(this.code(), (byte) slot, (byte) increment);
        }

        @Override
        public String toString() {
            return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
        }
        //</editor-fold>

    }

    /**
     * An instruction for widening another instruction.
     * This can be unreliable to use -- most instructions that support widening have support built in.
     *
     * @param mnemonic The operation code's reference name.
     * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
     *                 caution.
     */
    record Wide(String mnemonic, byte code) implements OpCode {

        //<editor-fold desc="WIDE" defaultstate="collapsed">
        @Override
        public int length() {
            return 6;
        }

        public UnboundedElement widen(CodeElement instruction) {
            return CodeElement.wide(instruction);
        }

        @Override
        public String toString() {
            return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
        }
        //</editor-fold>

    }

    /**
     * A variable loading/storing code. This has its own special handlers so that it can
     * default to the built-in instructions (e.g. `aload 0` -> `aload_0`) to save space
     * wherever possible.
     */
    abstract class VariableInstruction implements OpCode {

        //<editor-fold desc="Variable" defaultstate="collapsed">
        private final String mnemonic;
        private final byte code;

        public VariableInstruction(String mnemonic, byte code) {
            this.mnemonic = mnemonic;
            this.code = code;
        }

        public VariableInstruction(String mnemonic, int code) {
            this(mnemonic, (byte) code);
        }

        @Override
        public String mnemonic() {
            return mnemonic;
        }

        @Override
        public byte code() {
            return code;
        }

        @Override
        public int length() {
            return 2;
        }

        public CodeElement var(int slot) {
            U2.valueOf(slot);
            final int increment = switch (code) {
                case Codes.LLOAD, Codes.DLOAD -> 2;
                case Codes.LSTORE, Codes.DSTORE -> -2;
                default -> code < Codes.ISTORE ? 1 : -1;
            };
            if (slot > 255)
                return CodeElement.incrementStack(CodeElement.wide(CodeElement.fixed(code, (byte) (slot >>> 8),
                    (byte) (slot))), increment);
            return CodeElement.fixed(code, (byte) slot);
        }

        @Override
        public String toString() {
            return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
        }
        //</editor-fold>

    }

    /**
     * An opcode reference that doesn't have a strictly-defined type or data schema.
     * This is most likely a reserved/internal opcode, and/or one that is used by a specific VM/debugger.
     *
     * @param mnemonic The operation code's reference name.
     * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
     *                 caution.
     * @param length   The (expected) length of this instruction, including 1 for the code itself.
     *                 If the length is indeterminable (i.e. a conditional or variable-length instruction) then
     *                 this should return -1.
     */
    record UncheckedCode(String mnemonic, byte code, int length) implements OpCode {

        public UncheckedCode(String mnemonic, byte code) {
            this(mnemonic, code, -1);
        }

        @Override
        public String toString() {
            return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
        }

    }

}
