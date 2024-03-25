package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.constant.ConstantPoolInfo;
import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.assembler.vector.U1;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.UVec;
import mx.kenzie.foundation.detail.Type;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;
import java.lang.invoke.TypeDescriptor;

/**
 * Operation code.
 * An instruction in the virtual machine.
 */
public interface OpCode {

    Instruction AALOAD = new Instruction("AALOAD", Codes.AALOAD);
    Instruction AASTORE = new Instruction("AASTORE", Codes.AASTORE);
    Instruction ACONST_NULL = new Instruction("ACONST_NULL", Codes.ACONST_NULL);
    Instruction ALOAD_0 = new Instruction("ALOAD_0", Codes.ALOAD_0);
    Instruction ALOAD_1 = new Instruction("ALOAD_1", Codes.ALOAD_1);
    Instruction ALOAD_2 = new Instruction("ALOAD_2", Codes.ALOAD_2);
    Instruction ALOAD_3 = new Instruction("ALOAD_3", Codes.ALOAD_3);
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
    TypedInstruction ANEWARRAY = new TypedInstruction("ANEWARRAY", Codes.ANEWARRAY);
    Instruction ARETURN = new Instruction("ARETURN", Codes.ARETURN);
    Instruction ARRAYLENGTH = new Instruction("ARRAYLENGTH", Codes.ARRAYLENGTH);
    Instruction ASTORE_0 = new Instruction("ASTORE_0", Codes.ASTORE_0);
    Instruction ASTORE_1 = new Instruction("ASTORE_1", Codes.ASTORE_1);
    Instruction ASTORE_2 = new Instruction("ASTORE_2", Codes.ASTORE_2);
    Instruction ASTORE_3 = new Instruction("ASTORE_3", Codes.ASTORE_3);
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
    Instruction ATHROW = new Instruction("ATHROW", Codes.ATHROW);
    Instruction BALOAD = new Instruction("BALOAD", Codes.BALOAD);
    Instruction BASTORE = new Instruction("BASTORE", Codes.BASTORE);
    UncheckedCode BIPUSH = new UncheckedCode("BIPUSH", Codes.BIPUSH);
    Instruction CALOAD = new Instruction("CALOAD", Codes.CALOAD);
    Instruction CASTORE = new Instruction("CASTORE", Codes.CASTORE);
    TypedInstruction CHECKCAST = new TypedInstruction("CHECKCAST", Codes.CHECKCAST);
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
    Instruction DMUL = new Instruction("DMUL", Codes.DMUL);
    Instruction DNEG = new Instruction("DNEG", Codes.DNEG);
    Instruction DREM = new Instruction("DREM", Codes.DREM);
    Instruction DRETURN = new Instruction("DRETURN", Codes.DRETURN);
    Instruction DSTORE_0 = new Instruction("DSTORE_0", Codes.DSTORE_0);
    Instruction DSTORE_1 = new Instruction("DSTORE_1", Codes.DSTORE_1);
    Instruction DSTORE_2 = new Instruction("DSTORE_2", Codes.DSTORE_2);
    Instruction DSTORE_3 = new Instruction("DSTORE_3", Codes.DSTORE_3);
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
    Instruction FMUL = new Instruction("FMUL", Codes.FMUL);
    Instruction FNEG = new Instruction("FNEG", Codes.FNEG);
    Instruction FREM = new Instruction("FREM", Codes.FREM);
    Instruction FRETURN = new Instruction("FRETURN", Codes.FRETURN);
    Instruction FSTORE_0 = new Instruction("FSTORE_0", Codes.FSTORE_0);
    Instruction FSTORE_1 = new Instruction("FSTORE_1", Codes.FSTORE_1);
    Instruction FSTORE_2 = new Instruction("FSTORE_2", Codes.FSTORE_2);
    Instruction FSTORE_3 = new Instruction("FSTORE_3", Codes.FSTORE_3);
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
    Instruction FSUB = new Instruction("FSUB", Codes.FSUB);
    UncheckedCode GETFIELD = new UncheckedCode("GETFIELD", Codes.GETFIELD);
    UncheckedCode GETSTATIC = new UncheckedCode("GETSTATIC", Codes.GETSTATIC);
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
    Increment IINC = new Increment("IINC", Codes.IINC);
    Instruction ILOAD_0 = new Instruction("ILOAD_0", Codes.ILOAD_0);
    Instruction ILOAD_1 = new Instruction("ILOAD_1", Codes.ILOAD_1);
    Instruction ILOAD_2 = new Instruction("ILOAD_2", Codes.ILOAD_2);
    Instruction ILOAD_3 = new Instruction("ILOAD_3", Codes.ILOAD_3);
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
    Instruction IMUL = new Instruction("IMUL", Codes.IMUL);
    Instruction INEG = new Instruction("INEG", Codes.INEG);
    UncheckedCode INSTANCEOF = new UncheckedCode("INSTANCEOF", Codes.INSTANCEOF);
    UncheckedCode INVOKEDYNAMIC = new UncheckedCode("INVOKEDYNAMIC", Codes.INVOKEDYNAMIC);
    UncheckedCode INVOKEINTERFACE = new UncheckedCode("INVOKEINTERFACE", Codes.INVOKEINTERFACE);
    UncheckedCode INVOKESPECIAL = new UncheckedCode("INVOKESPECIAL", Codes.INVOKESPECIAL);
    UncheckedCode INVOKESTATIC = new UncheckedCode("INVOKESTATIC", Codes.INVOKESTATIC);
    UncheckedCode INVOKEVIRTUAL = new UncheckedCode("INVOKEVIRTUAL", Codes.INVOKEVIRTUAL);
    Instruction IOR = new Instruction("IOR", Codes.IOR);
    Instruction IREM = new Instruction("IREM", Codes.IREM);
    Instruction IRETURN = new Instruction("IRETURN", Codes.IRETURN);
    Instruction ISHL = new Instruction("ISHL", Codes.ISHL);
    Instruction ISHR = new Instruction("ISHR", Codes.ISHR);
    Instruction ISTORE_0 = new Instruction("ISTORE_0", Codes.ISTORE_0);
    Instruction ISTORE_1 = new Instruction("ISTORE_1", Codes.ISTORE_1);
    Instruction ISTORE_2 = new Instruction("ISTORE_2", Codes.ISTORE_2);
    Instruction ISTORE_3 = new Instruction("ISTORE_3", Codes.ISTORE_3);
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
    LoadConstant LDC = new LoadConstant("LDC", Codes.LDC);
    UncheckedCode LDC_W = new UncheckedCode("LDC_W", Codes.LDC_W);
    UncheckedCode LDC2_W = new UncheckedCode("LDC2_W", Codes.LDC2_W);
    Instruction LDIV = new Instruction("LDIV", Codes.LDIV);
    Instruction LLOAD_0 = new Instruction("LLOAD_0", Codes.LLOAD_0);
    Instruction LLOAD_1 = new Instruction("LLOAD_1", Codes.LLOAD_1);
    Instruction LLOAD_2 = new Instruction("LLOAD_2", Codes.LLOAD_2);
    Instruction LLOAD_3 = new Instruction("LLOAD_3", Codes.LLOAD_3);
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
    Instruction LSUB = new Instruction("LSUB", Codes.LSUB);
    Instruction LUSHR = new Instruction("LUSHR", Codes.LUSHR);
    Instruction LXOR = new Instruction("LXOR", Codes.LXOR);
    Instruction MONITORENTER = new Instruction("MONITORENTER", Codes.MONITORENTER);
    Instruction MONITOREXIT = new Instruction("MONITOREXIT", Codes.MONITOREXIT);
    UncheckedCode MULTIANEWARRAY = new UncheckedCode("MULTIANEWARRAY", Codes.MULTIANEWARRAY);
    UncheckedCode NEW = new UncheckedCode("NEW", Codes.NEW);
    UncheckedCode NEWARRAY = new UncheckedCode("NEWARRAY", Codes.NEWARRAY);
    Instruction NOP = new Instruction("NOP", Codes.NOP);
    Instruction POP = new Instruction("POP", Codes.POP);
    Instruction POP2 = new Instruction("POP2", Codes.POP2);
    UncheckedCode PUTFIELD = new UncheckedCode("PUTFIELD", Codes.PUTFIELD);
    UncheckedCode PUTSTATIC = new UncheckedCode("PUTSTATIC", Codes.PUTSTATIC);
    VariableInstruction RET = new VariableInstruction("RET", Codes.RET) {};
    Instruction RETURN = new Instruction("RETURN", Codes.RETURN);
    Instruction SALOAD = new Instruction("SALOAD", Codes.SALOAD);
    Instruction SASTORE = new Instruction("SASTORE", Codes.SASTORE);
    UncheckedCode SIPUSH = new UncheckedCode("SIPUSH", Codes.SIPUSH);
    Instruction SWAP = new Instruction("SWAP", Codes.SWAP);
    UncheckedCode TABLESWITCH = new UncheckedCode("TABLESWITCH", Codes.TABLESWITCH);
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
    record TypedInstruction(String mnemonic, byte code) implements OpCode {

        //<editor-fold desc="Make" defaultstate="collapsed">
        @Override
        public int length() {
            return 3;
        }

        public <Klass extends java.lang.reflect.Type & TypeDescriptor>
        UnboundedElement type(Klass type) {
            final Type value = Type.of(type);
            return storage -> new Typed(code, storage.constant(ConstantPoolInfo.TYPE, value));
        }

        private record Typed(byte code, PoolReference reference) implements RecordConstant, CodeElement {

            @Override
            public int length() {
                return 3;
            }

            @Override
            public void write(OutputStream stream) throws IOException {
                stream.write(code);
                this.reference.write(stream);
            }

        }

        @Override
        public String toString() {
            return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
        }

        //</editor-fold>

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
