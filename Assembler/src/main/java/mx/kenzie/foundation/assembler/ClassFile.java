package mx.kenzie.foundation.assembler;

import mx.kenzie.foundation.assembler.attribute.AttributeInfo;
import mx.kenzie.foundation.assembler.constant.ConstantPoolInfo;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

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

}
