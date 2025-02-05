package org.valross.foundation.assembler.code;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.constant.ConstantPoolInfo;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.tool.ProgramStack;
import org.valross.foundation.assembler.tool.StackNotifier;
import org.valross.foundation.detail.Type;
import org.valross.foundation.detail.TypeHint;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.TypeDescriptor;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * An instruction for loading a constant value.
 */
public class TypeCode implements OpCode {

    private final String mnemonic;
    private final byte code;
    private final BiConsumer<TypeHint, ProgramStack> notifier;

    /**
     * @param mnemonic The operation code's reference name.
     * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
     *                 caution.
     */
    public TypeCode(String mnemonic, byte code, BiConsumer<TypeHint, ProgramStack> notifier) {
        this.mnemonic = mnemonic;
        this.code = code;
        this.notifier = notifier;
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement type(Klass type) {
        final Type value = Type.of(type);
        return switch (code) {
            case Codes.NEW -> storage -> new New(value, storage.constant(ConstantPoolInfo.TYPE, value));
            default -> storage -> CodeElement.notify(new Typed(code, storage.constant(ConstantPoolInfo.TYPE, value)),
                                                     builder -> notifier.accept(value, builder.stack()));
        };
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
        return this.mnemonic.toLowerCase() + "/" + Byte.toUnsignedInt(code);
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

    public record New(Type value, PoolReference reference) implements CodeElement, RecordConstant {

        @Override
        public void write(OutputStream stream) throws IOException {
            stream.write(this.code());
            this.reference.write(stream);
        }

        @Override
        public void notify(CodeBuilder builder) {
            if (!builder.trackStack()) return;
            builder.stack().push(TypeHint.uninitialised(value, this, builder.vector()));
            CodeElement.super.notify(builder);
        }

        @Override
        public byte code() {
            return Codes.NEW;
        }

        @Override
        public int length() {
            return 3;
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
            super(mnemonic, code, null);
        }

        public <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement type(Klass type) {
            final Type value = Type.of(type);
            return switch (value.descriptorString()) {
                case "Z" -> this.primitive(4, Type.of(boolean[].class));
                case "C" -> this.primitive(5, Type.of(char[].class));
                case "F" -> this.primitive(6, Type.of(float[].class));
                case "D" -> this.primitive(7, Type.of(double[].class));
                case "B" -> this.primitive(8, Type.of(byte[].class));
                case "S" -> this.primitive(9, Type.of(short[].class));
                case "I" -> this.primitive(10, Type.of(int[].class));
                case "J" -> this.primitive(11, Type.of(long[].class));
                default ->
                    storage -> CodeElement.notify(new Typed(Codes.ANEWARRAY, storage.constant(ConstantPoolInfo.TYPE,
                                                                                              value)),
                                                  StackNotifier.pop1push(value.arrayType()));
            };
        }

        public CodeElement primitive(int arrayType, TypeHint type) {
            return CodeElement.notify(CodeElement.fixed(code(), (byte) arrayType), StackNotifier.pop1push(type));
        }

        public CodeElement primitive(int arrayType) {
            return this.primitive(arrayType, switch (arrayType) {
                case 4 -> Type.of(boolean[].class);
                case 5 -> Type.of(char[].class);
                case 6 -> Type.of(float[].class);
                case 7 -> Type.of(double[].class);
                case 8 -> Type.of(byte[].class);
                case 9 -> Type.of(short[].class);
                case 10 -> Type.of(int[].class);
                case 11 -> Type.of(long[].class);
                default -> throw new IllegalArgumentException(arrayType + "");
            });
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
            super(mnemonic, code, null);
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
            record MultiNewArray(PoolReference reference, int dimensions, Type type)
                implements CodeElement, RecordConstant {

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
                    if (!builder.trackStack()) return;
                    try {
                        builder.stack().pop(MultiNewArray.this.dimensions);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        throw new IndexOutOfBoundsException("Tried to pop " + MultiNewArray.this.dimensions
                                                                + " integers for multi-array creation, but the stack " +
                                                                "under-flowed.");
                    }
                    builder.stack().push(type);
                }

                @Override
                public byte code() {
                    return Codes.MULTIANEWARRAY;
                }

            }
            //</editor-fold>
            return storage -> new MultiNewArray(storage.constant(ConstantPoolInfo.TYPE, value), dimensions, value);
        }

    }

//</editor-fold>

}
