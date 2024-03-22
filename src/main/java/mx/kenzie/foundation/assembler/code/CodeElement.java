package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import org.valross.constantine.Constant;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

import static mx.kenzie.foundation.assembler.code.Codes.*;

public interface CodeElement extends Data, UVec, UnboundedElement {

    static CodeElement fixed(byte... bytes) {
        return new FixedCodeElement(bytes);
    }

    static CodeElement wide(CodeElement element) {
        return new FixedCodeElement(WIDE, element);
    }

    static int knownStackIncrement(int opcode) {
        switch (opcode) {
            case NOP, SWAP, IINC, GOTO, GOTO_W, RET, RETURN, GETFIELD, NEWARRAY, ANEWARRAY, ARRAYLENGTH, CHECKCAST,
                 INSTANCEOF:
                return 0;
            case ACONST_NULL, LDC, LDC_W, DUP, DUP_X1, DUP_X2, I2L, I2D, F2D, F2L, JSR, JSR_W, GETSTATIC, NEW:
                return 1;
            case LDC2_W, LLOAD, DLOAD, LALOAD, DALOAD, DUP2, DUP2_X1, DUP2_X2:
                return 2;
            case LSTORE, DSTORE, LASTORE, DASTORE, POP2, LADD, DADD, LSUB, DSUB, LMUL, DMUL, LDIV, DDIV, LREM, DREM,
                 LAND, LOR, LXOR, LRETURN, DRETURN, PUTFIELD:
                return -2;
            case POP, L2F, L2I, D2F, D2I, FCMPL, FCMPG, TABLESWITCH, LOOKUPSWITCH, PUTSTATIC, ATHROW, MONITORENTER,
                 MONITOREXIT, IFNULL, IFNONNULL:
                return -1;
            case LCMP, DCMPG, DCMPL:
                return -3;
            case INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE, INVOKEDYNAMIC:
                throw new IllegalArgumentException("Invoke opcode requires dynamic stack size calculation");
            case WIDE, MULTIANEWARRAY:
                throw new IllegalArgumentException("Opcode requires dynamic stack size calculation");
        }
        if (opcode < 9) return 1;
        if (opcode < 11) return 2; // lconst
        if (opcode < 14) return 1;
        if (opcode < 16) return 2; // dconst
        if (opcode < 30) return 1;
        if (opcode < 34) return 2; // lload
        if (opcode < 38) return 1;
        if (opcode < 42) return 2; // dload
        if (opcode < 54) return 1;
        if (opcode < 63) return -1;
        if (opcode < 67) return -2; // lstore
        if (opcode < 71) return -1;
        if (opcode < 75) return -2; // dstore
        if (opcode < 87) return -1;
        if (opcode < 116) return -1;
        if (opcode < 126) return 0;
        if (opcode < 132) return -1;
        if (opcode < 148) return 0;
        if (opcode < 159) return -1;
        if (opcode < 167) return -2;
        if (opcode < 178) return -1;
        throw new IllegalArgumentException("Unknown opcode " + opcode + " has unknown behaviour");
    }

    static CodeElement incrementStack(final CodeElement element, final int increment) {
        //<editor-fold desc="Wrapper" defaultstate="collapsed">
        class WrapperElement implements CodeElement {

            final CodeElement element;
            final int increment;

            WrapperElement(CodeElement element, int increment) {
                this.element = element;
                this.increment = increment;
            }

            @Override
            public void notify(CodeBuilder builder) {
                builder.notifyStack(increment);
                this.element.notify(builder);
            }

            @Override
            public byte code() {
                return element.code();
            }

            @Override
            public int length() {
                return element.length();
            }

            @Override
            public Constant constant() {
                return element.constant();
            }

        }
        //</editor-fold>
        return new WrapperElement(element, increment);
    }

    static CodeElement notifyVariable(final CodeElement element, final int slot) {
        //<editor-fold desc="Wrapper" defaultstate="collapsed">
        class WrapperElement implements CodeElement {

            final CodeElement element;
            final int slot;

            WrapperElement(CodeElement element, int slot) {
                this.element = element;
                this.slot = slot;
            }

            @Override
            public void notify(CodeBuilder builder) {
                builder.notifyMaxLocalIndex(slot);
                this.element.notify(builder);
            }

            @Override
            public byte code() {
                return element.code();
            }

            @Override
            public int length() {
                return element.length();
            }

            @Override
            public Constant constant() {
                return element.constant();
            }

        }
        //</editor-fold>
        return new WrapperElement(element, slot);
    }

    default @Override CodeElement bound(ClassFileBuilder.Storage storage) {
        return this;
    }

    default void notify(CodeBuilder builder) {
        final int stack = knownStackIncrement(Byte.toUnsignedInt(this.code()));
        builder.notifyStack(stack);
    }

    byte code();

}

record FixedCodeElement(byte[] binary) implements CodeElement, RecordConstant {

    public FixedCodeElement(byte code, CodeElement inner) {
        this(join(code, inner.binary()));
    }

    private static byte[] join(byte b, byte[] binary) {
        final byte[] array = new byte[binary.length + 1];
        array[0] = b;
        System.arraycopy(binary, 1, array, 0, binary.length);
        return array;
    }

    @Override
    public int length() {
        return binary.length;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        stream.write(binary);
    }

    @Override
    public byte code() {
        return binary[0];
    }

}