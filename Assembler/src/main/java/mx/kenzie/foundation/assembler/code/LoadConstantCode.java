package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.constant.ConstantPoolInfo;
import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.detail.Type;
import mx.kenzie.foundation.detail.TypeHint;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.constant.Constable;

/**
 * An instruction for loading a constant value.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record LoadConstantCode(String mnemonic, byte code) implements OpCode {

    //<editor-fold desc="LDC" defaultstate="collapsed">
    @Override
    public int length() {
        return 1;
    }

    public UnboundedElement value(Constable value) {
        return switch (value) {
            case null -> ACONST_NULL;
            case Long j -> storage -> new WideType(Type.LONG, storage.constant(ConstantPoolInfo.LONG, j));
            case Double d -> storage -> new WideType(Type.DOUBLE, storage.constant(ConstantPoolInfo.DOUBLE, d));
            case Number _, Boolean _, Character _ -> storage -> new NarrowType(Type.INT, storage.constant(value));
            default -> storage -> new NarrowType(Type.of(value.getClass()), storage.constant(value));
        };
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
    }

    private record NarrowType(TypeHint hint, PoolReference reference) implements CodeElement, RecordConstant {

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
            if (!builder.trackStack()) return;
            builder.stack().push(hint);
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

    private record WideType(TypeHint hint, PoolReference reference) implements CodeElement, RecordConstant {

        @Override
        public void write(OutputStream stream) throws IOException {
            stream.write(Codes.LDC2_W);
            this.reference.write(stream);
        }

        @Override
        public void notify(CodeBuilder builder) {
            if (!builder.trackStack()) return;
            builder.stack().push(hint);
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
