package mx.kenzie.foundation.instruction;

import org.valross.foundation.assembler.code.OpCode;
import org.valross.foundation.detail.Erasure;
import org.valross.foundation.detail.Member;
import org.valross.foundation.detail.Signature;
import org.valross.foundation.detail.Type;

import java.lang.invoke.TypeDescriptor;

public class AccessField {

    AccessField() {
    }

    public <Klass extends java.lang.reflect.Type & TypeDescriptor>
    Stub of(Klass owner, String name, Klass type) {
        return new ConstantStub(Type.of(owner), Type.of(type), name);
    }

    public interface Stub extends Erasure {

        default <Result> Instruction.Input<Result> get() {
            return builder -> builder.write(OpCode.GETSTATIC.field(this.owner(), this));
        }

        default <Result> Instruction.Input<Result> get(Instruction.Input<Object> object) {
            return builder -> {
                object.write(builder);
                builder.write(OpCode.GETSTATIC.field(this.owner(), this));
            };
        }

        default Instruction.Base set(Instruction.Input<?> value) {
            return builder -> {
                value.write(builder);
                builder.write(OpCode.PUTSTATIC.field(this.owner(), this));
            };
        }

        default Instruction.Base set(Instruction.Input<Object> object, Instruction.Input<?> value) {
            return builder -> {
                object.write(builder);
                value.write(builder);
                builder.write(OpCode.PUTFIELD.field(this.owner(), this));
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

        @Override
        default boolean isMethod() {
            return false;
        }

        @Override
        default boolean isField() {
            return true;
        }

        default Signature asSignature() {
            return new Signature(name(), type());
        }

        default Member asMember() {
            return new Member(owner(), asSignature());
        }

    }

    public record ConstantStub(Type owner, Type type, String name) implements Stub {

    }

}
