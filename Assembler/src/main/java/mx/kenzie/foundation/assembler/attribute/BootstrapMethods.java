package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.tool.AttributeBuilder;
import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import mx.kenzie.foundation.assembler.vector.U2;
import mx.kenzie.foundation.assembler.vector.U4;
import mx.kenzie.foundation.assembler.vector.UVec;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public record BootstrapMethods(PoolReference attribute_name_index, BootstrapMethod... bootstrap_methods)
    implements AttributeInfo.TypeAttribute, AttributeBuilder, AttributeInfo, UVec, RecordConstant {

    public BootstrapMethods(ClassFileBuilder.Storage storage, BootstrapMethod... bootstrap_methods) {
        this(storage.constant(UTF8, "BootstrapMethods"), bootstrap_methods);
    }

    public U2 num_bootstrap_methods() {
        return U2.valueOf(bootstrap_methods.length);
    }

    @Override
    public U4 attribute_length() {
        int length = 2;
        for (BootstrapMethod method : bootstrap_methods) length += method.length();
        return U4.valueOf(length);
    }

    @Override
    public UVec info() {
        return UVec.of(num_bootstrap_methods(), UVec.of(bootstrap_methods));
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.attribute_name_index.write(stream);
        this.attribute_length().write(stream);
        this.num_bootstrap_methods().write(stream);
        for (BootstrapMethod method : bootstrap_methods) method.write(stream);
    }

    @Override
    public void debug(String indent, PrintStream stream) {
        stream.print(indent);
        stream.println(this.attributeName());
        stream.print(indent + "\t");
        stream.println("attribute_name_index = " + Arrays.toString(attribute_name_index.binary()));
        stream.print(indent + "\t");
        stream.println("attribute_length = " + Arrays.toString(attribute_length().binary()));
        stream.print(indent + "\t");
        stream.println("num_bootstrap_methods = " + Arrays.toString(num_bootstrap_methods().binary()));
        for (BootstrapMethod method : bootstrap_methods) {
            stream.print(indent + "\tmethod (" + (2 + 2 + (2 * method.bootstrap_arguments.length)) + "/" + method.binary().length + ") ");
            stream.println(Arrays.toString(method.binary()));
            stream.print(indent + "\t");
            stream.println(method);
        }
    }

    public record BootstrapMethod(PoolReference bootstrap_method_ref, PoolReference... bootstrap_arguments)
        implements UVec, Data, RecordConstant {

        public U2 num_bootstrap_arguments() {
            return U2.valueOf(bootstrap_arguments.length);
        }

        @Override
        public int length() {
            return 4 + (bootstrap_arguments.length * 2);
        }

        @Override
        public void write(OutputStream stream) throws IOException {
            this.bootstrap_method_ref.write(stream);
            this.num_bootstrap_arguments().write(stream);
            for (PoolReference argument : bootstrap_arguments) argument.write(stream);
        }

    }

}
