package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.constant.ConstantPoolInfo;
import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.detail.Type;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.TypeDescriptor;
import java.util.Objects;

/**
 * An instruction for loading a constant value.
 */
public class TypeCode implements OpCode {

    private final String mnemonic;
    private final byte code;

    /**
     * @param mnemonic The operation code's reference name.
     * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
     *                 caution.
     */
    public TypeCode(String mnemonic, byte code) {
        this.mnemonic = mnemonic;
        this.code = code;
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement type(Klass type) {
        final Type value = Type.of(type);
        return storage -> new Typed(code, storage.constant(ConstantPoolInfo.TYPE, value));
    }

    @Override
    public String mnemonic() {
        return mnemonic;
    }

    @Override
    public byte code() {
        return code;
    }

    //<editor-fold desc="Create type instruction" defaultstate="collapsed">
    @Override
    public int length() {
        return 3;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mnemonic, code);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TypeCode) obj;
        return Objects.equals(this.mnemonic, that.mnemonic) &&
            this.code == that.code;
    }

    @Override
    public String toString() {
        return this.mnemonic.toLowerCase() + "/" + Integer.toUnsignedString(code);
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

    /**
     * A special version of the type instruction for array codes (anewarray, newarray).
     * This will automatically switch to the correct choice out of the primitive/object codes
     * if there is any discrepancy in the type (e.g. the user attempts to make an object array of a primitive type).
     */
    public static class Array extends TypeCode {

        /**
         * @param mnemonic The operation code's reference name.
         * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
         *                 caution.
         */
        public Array(String mnemonic, byte code) {
            super(mnemonic, code);
        }

        public <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement type(Klass type) {
            final Type value = Type.of(type);
            return switch (value.descriptorString()) {
                case "Z" -> this.primitive(4);
                case "C" -> this.primitive(5);
                case "F" -> this.primitive(6);
                case "D" -> this.primitive(7);
                case "B" -> this.primitive(8);
                case "S" -> this.primitive(9);
                case "I" -> this.primitive(10);
                case "J" -> this.primitive(11);
                default -> storage -> new Typed(Codes.ANEWARRAY, storage.constant(ConstantPoolInfo.TYPE, value));
            };
        }

        public CodeElement primitive(int arrayType) {
            return CodeElement.fixed(code(), (byte) arrayType);
        }

    }

    /**
     * A special version of the type instruction for multidimensional arrays.
     */
    public static class MultiArray extends TypeCode {

        /**
         * @param mnemonic The operation code's reference name.
         * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
         *                 caution.
         */
        public MultiArray(String mnemonic, byte code) {
            super(mnemonic, code);
        }

        public <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement type(Klass type) {
            return this.type(type, 0);
        }

        public <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement type(Klass type,
                                                                                             int dimensions) {
            var check = Type.of(type); // type needs at least <dimensions> dimensions.
            while (check.arrayDepth() < dimensions) check = check.arrayType();
            final Type value = check;
            //<editor-fold desc="Instruction" defaultstate="collapsed">
            record MultiNewArray(PoolReference reference, int dimensions) implements CodeElement, RecordConstant {

                @Override
                public int length() {
                    return 4;
                }

                @Override
                public void write(OutputStream stream) throws IOException {
                    stream.write(this.code());
                    this.reference.write(stream);
                    stream.write(MultiNewArray.this.dimensions);
                }

                @Override
                public void notify(CodeBuilder builder) {
                    builder.notifyStack(1 - MultiNewArray.this.dimensions);
                }

                @Override
                public byte code() {
                    return Codes.MULTIANEWARRAY;
                }

            }
            //</editor-fold>
            return storage -> new MultiNewArray(storage.constant(ConstantPoolInfo.TYPE, value), dimensions);
        }

    }

//</editor-fold>

}
