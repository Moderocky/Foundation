package org.valross.foundation.assembler.code;

import org.valross.constantine.Constant;
import org.valross.constantine.Constantive;
import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.Data;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

import static org.valross.foundation.assembler.code.Codes.*;

public interface CodeElement extends Data, UVec, UnboundedElement {

    static CodeElement fixed(byte... bytes) {
        return new FixedCodeElement(bytes);
    }

    static CodeElement vector(byte code, UVec data) {
        //<editor-fold desc="Vector Element" defaultstate="collapsed">
        record VectorElement(byte code, UVec data) implements CodeElement, Constantive {

            @Override
            public byte code() {
                return code;
            }

            @Override
            public int length() {
                return 1 + data.length();
            }

            @Override
            public void write(OutputStream stream) throws IOException {
                stream.write(code);
                this.data.write(stream);
            }

            @Override
            public FixedCodeElement constant() {
                final byte[] bytes = VectorElement.this.data.binary(), array = new byte[bytes.length + 1];
                System.arraycopy(bytes, 0, array, 1, bytes.length);
                array[0] = VectorElement.this.code;
                return (FixedCodeElement) fixed(array);
            }

        }
        //</editor-fold>
        return new VectorElement(code, data);
    }

    static CodeElement wide(CodeElement element) {
        return new FixedCodeElement(WIDE, element);
    }

    static int knownStackIncrement(int opcode) {
        switch ((byte) opcode) {
            case NOP, SWAP, IINC, GOTO, GOTO_W, RET, RETURN, GETFIELD, NEWARRAY, ANEWARRAY, ARRAYLENGTH, CHECKCAST,
                 INSTANCEOF, WIDE:
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
            case MULTIANEWARRAY:
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
            public byte[] binary() {
                return element.binary();
            }

            @Override
            public Constant constant() {
                return element.constant();
            }

        }
        //</editor-fold>
        return new WrapperElement(element, increment);
    }

    static CodeElement notify(final CodeElement element, Consumer<CodeBuilder> notifier) {
        //<editor-fold desc="Wrapper" defaultstate="collapsed">
        record WrapperElement(CodeElement element, Consumer<CodeBuilder> notifier)
            implements CodeElement, RecordConstant {

            @Override
            public void notify(CodeBuilder builder) {
                this.notifier.accept(builder);
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
            public byte[] binary() {
                return element.binary();
            }

        }
        //</editor-fold>
        return new WrapperElement(element, notifier);
    }

    default @Override CodeElement bound(ClassFileBuilder.Storage storage) {
        return this;
    }

    /**
     * Called when this element is inserted into a builder (e.g. if it needs to edit its neighbours)
     */
    default void insert(CodeBuilder builder) {

    }

    /**
     * Called for each element in succession to build the stack map.
     */
    default void notify(CodeBuilder builder) {

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
        System.arraycopy(binary, 0, array, 1, binary.length);
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