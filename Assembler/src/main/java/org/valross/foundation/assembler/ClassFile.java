package org.valross.foundation.assembler;

import org.valross.constantine.RecordConstant;
import org.valross.foundation.assembler.attribute.AttributeInfo;
import org.valross.foundation.assembler.constant.ConstantPoolInfo;
import org.valross.foundation.assembler.tool.PoolReference;
import org.valross.foundation.assembler.vector.U2;
import org.valross.foundation.assembler.vector.U4;
import org.valross.foundation.assembler.vector.UVec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public record ClassFile(U4 magic, U2 minor_version, U2 major_version, U2 constant_pool_count,
                        ConstantPoolInfo[] constant_pool,  //constant_pool_count-1
                        U2 access_flags, PoolReference this_class, PoolReference super_class, U2 interfaces_count,
                        PoolReference[] interfaces,
                        //interfaces_count
                        U2 fields_count, FieldInfo[] fields, //fields_count
                        U2 methods_count, MethodInfo[] methods, //methods_count
                        U2 attributes_count, AttributeInfo[] attributes //attributes_count
) implements Data, UVec, RecordConstant {

    @Override
    public boolean validate() {
        if (constant_pool.length != constant_pool_count.value() - 1) return false;
        if (fields.length != fields_count.value()) return false;
        if (methods.length != methods_count.value()) return false;
        if (attributes.length != attributes_count.value()) return false;
        return RecordConstant.super.validate();
    }

    @Override
    public int length() {
        int count = 0;
        count += this.magic.length();
        count += this.minor_version.length();
        count += this.major_version.length();
        count += this.constant_pool_count.length();
        for (Data info : constant_pool) count += info.length();
        count += this.access_flags.length();
        count += this.this_class.length();
        count += this.super_class.length();
        count += this.interfaces_count.length();
        for (Data info : interfaces) count += info.length();
        count += this.fields_count.length();
        for (Data info : fields) count += info.length();
        count += this.methods_count.length();
        for (Data info : methods) count += info.length();
        count += this.attributes_count.length();
        for (Data info : attributes) count += info.length();
        return count;
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.magic.write(stream);
        this.minor_version.write(stream);
        this.major_version.write(stream);
        this.constant_pool_count.write(stream);
        for (Data info : constant_pool) info.write(stream);
        this.access_flags.write(stream);
        this.this_class.write(stream);
        this.super_class.write(stream);
        this.interfaces_count.write(stream);
        for (Data info : interfaces) info.write(stream);
        this.fields_count.write(stream);
        for (Data info : fields) info.write(stream);
        this.methods_count.write(stream);
        for (Data info : methods) info.write(stream);
        this.attributes_count.write(stream);
        for (Data info : attributes) info.write(stream);
    }

    public void debug(PrintStream stream) {
        stream.println("Expected length: " + this.length());
        stream.println("Actual length: " + this.binary().length);
        stream.println("magic (" + magic.length() + "/" + magic.binary().length + "): " + Arrays.toString(magic.binary()));
        stream.println("minor_version (" + minor_version.length() + "/" + minor_version.binary().length + "): " + Arrays.toString(minor_version.binary()));
        stream.println("major_version (" + major_version.length() + "/" + major_version.binary().length + "): " + Arrays.toString(major_version.binary()));
        stream.println("constant_pool_count (" + constant_pool_count.length() + "/" + constant_pool_count.binary().length + "): " + Arrays.toString(constant_pool_count.binary()));
        for (int i = 0; i < constant_pool.length; i++) {
            final ConstantPoolInfo info = constant_pool[i];
            stream.print("\t");
            final int expectedLength = info.length(), realLength = info.binary().length;
            stream.print(i + " " + info.getClass().getSimpleName() + " (" + expectedLength + "/" + realLength + "): ");
            stream.println(Arrays.toString(info.binary()));
            stream.println("\t" + info);
            if (expectedLength == realLength) continue;
            stream.print("\t");
            stream.println("ERROR in " + info);
            assert Arrays.equals(info.binary(), this.write(info));
        }
        stream.println("access_flags (" + access_flags.length() + "/" + access_flags.binary().length + "): " + Arrays.toString(access_flags.binary()));
        stream.println("this_class (" + this_class.length() + "/" + this_class.binary().length + "): " + Arrays.toString(this_class.binary()));
        stream.println("super_class (" + super_class.length() + "/" + super_class.binary().length + "): " + Arrays.toString(super_class.binary()));
        stream.println("interfaces_count (" + interfaces_count.length() + "/" + interfaces_count.binary().length + ")" +
                           ": " + Arrays.toString(interfaces_count.binary()));
        for (int i = 0; i < interfaces.length; i++) {
            stream.print("\t");
            stream.print(i + " (" + interfaces[i].length() + "/" + interfaces[i].binary().length + "): ");
            stream.println(Arrays.toString(interfaces[i].binary()));
            assert Arrays.equals(interfaces[i].binary(), this.write(interfaces[i]));
        }
        stream.println("fields_count (" + fields_count.length() + "/" + fields_count.binary().length + "): " + Arrays.toString(fields_count.binary()));
        for (int i = 0; i < fields.length; i++) {
            stream.print("\t");
            stream.print(i + " (" + fields[i].length() + "/" + fields[i].binary().length + "): ");
            stream.println(Arrays.toString(fields[i].binary()));
            assert Arrays.equals(fields[i].binary(), this.write(fields[i]));
        }
        stream.println("methods_count (" + methods_count.length() + "/" + methods_count.binary().length + "): " + Arrays.toString(methods_count.binary()));
        for (int i = 0; i < methods.length; i++) {
            stream.print("\t");
            final MethodInfo method = methods[i];
            stream.print(i + " (" + method.length() + "/" + method.binary().length + "): ");
            stream.println(Arrays.toString(method.binary()));
            for (AttributeInfo info : method.attributes()) info.debug("\t", stream);
            assert Arrays.equals(method.binary(), this.write(method));
        }
        stream.println("attributes_count (" + attributes_count.length() + "/" + attributes_count.binary().length + ")" +
                           ": " + Arrays.toString(attributes_count.binary()));
        for (int i = 0; i < attributes.length; i++) {
            stream.print("\t");
            stream.print(i + " (" + attributes[i].length() + "/" + attributes[i].binary().length + "): ");
            stream.println(Arrays.toString(attributes[i].binary()));
            attributes[i].debug("\t", stream);
            assert Arrays.equals(attributes[i].binary(), this.write(attributes[i]));
        }
        stream.println(Arrays.toString(this.binary()));
    }

    private byte[] write(Data data) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            data.write(stream);
        } catch (IOException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return stream.toByteArray();
    }

}
