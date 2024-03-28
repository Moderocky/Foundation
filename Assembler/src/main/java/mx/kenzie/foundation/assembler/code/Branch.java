package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.valross.constantine.Constant;

import java.io.IOException;
import java.io.OutputStream;

public class Branch implements CodeElement {

    protected Handle handle = new Handle();

    @Override
    public byte[] binary() {
        return new byte[0];
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public void write(OutputStream stream) {
    }

    @Override
    public byte code() {
        return -1;
    }

    @Override
    public void notify(CodeBuilder builder) {
        this.handle.setVector(builder.vector());
    }

    @Override
    public Constant constant() {
        return UVec.of(this.binary());
    }

    protected Handle getHandle() {
        return handle;
    }

    protected class Handle implements UVec {

        protected CodeVector vector;

        protected Handle(CodeVector vector) {
            this.vector = vector;
        }

        protected Handle() {
            this(null);
        }

        public void setVector(CodeVector vector) {
            this.vector = vector;
        }

        public int index() {
            if (vector == null) return -1;
            int index = 0;
            for (CodeElement element : vector.code) {
                if (element == Branch.this) return index;
                else index += element.length();
            }
            return -1;
        }

        public boolean wide() {
            return this.index() > 65565;
        }

        @Override
        public byte[] binary() {
            final short value = (short) this.index();
            return new byte[] {
                (byte) (value >>> 8),
                (byte) (value)
            };
        }

        @Override
        public void write(OutputStream stream) throws IOException {
            final short value = (short) this.index();
            stream.write((value >>> 8));
            stream.write(value);
        }

        @Override
        public int length() {
            return this.wide() ? 4 : 2;
        }

        @Override
        public Constant constant() {
            return UVec.of(this.binary());
        }

    }

}
