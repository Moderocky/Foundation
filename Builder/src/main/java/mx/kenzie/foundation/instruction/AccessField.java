package mx.kenzie.foundation.instruction;

import mx.kenzie.foundation.detail.Erasure;
import mx.kenzie.foundation.detail.Member;
import mx.kenzie.foundation.detail.Signature;
import mx.kenzie.foundation.detail.Type;

import java.lang.invoke.TypeDescriptor;

import static org.objectweb.asm.Opcodes.*;

public class AccessField {

    AccessField() {
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass owner, String name, Klass type) {
        return new ConstantStub(Type.of(owner), Type.of(type), name);
    }

    public interface Stub extends Erasure {

        default <Result> Instruction.Input<Result> get() {
            return visitor -> visitor.visitFieldInsn(GETSTATIC, this.owner().internalName(), this.name(),
                                                     this.type().descriptorString());
        }

        default <Result> Instruction.Input<Result> get(Instruction.Input<Object> object) {
            return visitor -> {
                object.write(visitor);
                visitor.visitFieldInsn(GETFIELD, this.owner().internalName(), this.name(),
                                       this.type().descriptorString());
            };
        }

        default Instruction.Base set(Instruction.Input<?> value) {
            return visitor -> {
                value.write(visitor);
                visitor.visitFieldInsn(PUTSTATIC, this.owner().internalName(), this.name(),
                                       this.type().descriptorString());
            };
        }

        default Instruction.Base set(Instruction.Input<Object> object, Instruction.Input<?> value) {
            return visitor -> {
                object.write(visitor);
                value.write(visitor);
                visitor.visitFieldInsn(PUTFIELD, this.owner().internalName(), this.name(),
                                       this.type().descriptorString());
            };
        }

        Type owner();

        Type type();

        @Override
        default Type returnType() {
            return this.type();
        }

        String name();

        @Override
        default Type[] parameters() {
            return new Type[0];
        }

        @Override
        default String descriptorString() {
            return this.returnType().descriptorString();
        }

        default Signature asSignature() {
            return new Signature(name(), type());
        }

        default Member asMember() {
            return new Member(owner(), asSignature());
        }

        @Override
        default boolean isMethod() {
            return false;
        }

        @Override
        default boolean isField() {
            return true;
        }

    }

    public record ConstantStub(Type owner, Type type, String name) implements Stub {

    }

}
