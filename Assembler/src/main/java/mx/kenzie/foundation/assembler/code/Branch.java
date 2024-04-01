package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.error.IncompatibleBranchError;
import mx.kenzie.foundation.assembler.tool.CodeBuilder;
import mx.kenzie.foundation.assembler.tool.ProgramRegister;
import mx.kenzie.foundation.assembler.tool.ProgramStack;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.UVec;
import mx.kenzie.foundation.detail.Type;
import mx.kenzie.foundation.detail.TypeHint;
import org.valross.constantine.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class Branch implements CodeElement {

    protected Handle handle = new Handle();
    protected TypeHint[] stack, register;

    public Frame.Map toStackMap() {
        return new Frame.Map(register, stack);
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public byte[] binary() {
        return new byte[0];
    }

    @Override
    public void write(OutputStream stream) {
    }

    @Override
    public void notify(CodeBuilder builder) {
        this.handle.setVector(builder);
        if (builder.trackFrames()) this.checkFrame(builder.stack(), builder.register());
        if (builder.trackStack()) {
            builder.stack().reframe(this.stack);
            builder.register().reframe(this.register);
        }
    }

    @Override
    public void insert(CodeBuilder builder) {
        this.handle.setVector(builder);
        CodeElement.super.insert(builder);
    }

    @Override
    public byte code() {
        return -1;
    }

    @Override
    public Constant constant() {
        return UVec.of(this.binary());
    }

    protected Handle getHandle() {
        return handle;
    }

    protected UVec getJump(CodeElement source) {
        final int target = this.getHandle().index();
        int index = 0;
        for (CodeElement element : this.handle.vector.code) {
            if (element == source) break;
            else index += element.length();
        }
        final short jump = (short) (target - index);
        return U2.valueOf(jump);
    }

    public void checkFrame(ProgramStack stack, ProgramRegister register) {
        if (this.stack == null || this.stack.length == 0) {
            this.stack = stack.toArray();
        } else if (this.stack.length > 0 && !stack.isEmpty() && !Arrays.equals(this.stack, stack.toArray()))
            throw new IncompatibleBranchError("Expected stack to be " + this.printTable(this.stack) + " entering" +
                                                  " " +
                                                  this + " but found " + this.printTable(stack.toArray()));
        if (this.register == null || this.register.length == 0) {
            this.register = register.toArray();
        } else if (!stack.isEmpty() && !Arrays.equals(this.register, register.toArray()))
            throw new IncompatibleBranchError("Expected register to be " + this.printTable(this.register) + " " +
                                                  "entering " +
                                                  this + " but found " + this.printTable(register.toArray()));
    }

    private String printTable(TypeHint[] array) {
        return '[' + String.join(", ", Arrays.stream(array).map(TypeHint::getTypeName).toList()) + ']';
    }

    @Override
    public String toString() {
        if (handle.vector == null) return "Branch";
        return "Branch[index=" + handle.index() + "]";
    }

    public static class ImplicitBranch extends Branch {

        public ImplicitBranch(Type... parameters) {
            this.register = parameters;
        }

    }

    public static class UnconditionalBranch extends Branch {

    }

    protected class Handle implements UVec {

        protected CodeBuilder builder;
        protected CodeVector vector;

        protected Handle(CodeBuilder builder) {
            this.setVector(builder);
        }

        protected Handle() {
            this(null);
        }

        public void setVector(CodeBuilder builder) {
            this.builder = builder;
            if (builder != null) vector = builder.vector();
            else vector = null;
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
            return false; // wide jumps aren't supported by the jvm (yet)
            // return this.index() > 65565;
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
        public byte[] binary() {
            final short value = (short) this.index();
            return new byte[] {(byte) (value >>> 8), (byte) (value)};
        }

        @Override
        public Constant constant() {
            return UVec.of(this.binary());
        }

        public Branch branch() {
            return Branch.this;
        }

    }

}
