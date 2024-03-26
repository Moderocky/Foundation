package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.constant.ConstantPoolInfo;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.detail.Type;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.TypeDescriptor;

/**
 * An instruction for loading a constant value.
 *
 * @param mnemonic The operation code's reference name.
 * @param code     The byte code. This is likely to be an UNSIGNED byte in disguise, so should be treated with
 *                 caution.
 */
public record TypeCode(String mnemonic, byte code) implements OpCode {

    //<editor-fold desc="Create type instruction" defaultstate="collapsed">
    @Override
    public int length() {
        return 3;
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor> UnboundedElement type(Klass type) {
        final Type value = Type.of(type);
        return storage -> new Typed(code, storage.constant(ConstantPoolInfo.TYPE, value));
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

    //</editor-fold>

}
