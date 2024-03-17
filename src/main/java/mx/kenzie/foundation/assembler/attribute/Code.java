package mx.kenzie.foundation.assembler.attribute;

import mx.kenzie.foundation.assembler.Data;
import mx.kenzie.foundation.assembler.U2;
import mx.kenzie.foundation.assembler.U4;
import mx.kenzie.foundation.assembler.UVec;
import mx.kenzie.foundation.assembler.tool.ClassFileBuilder;
import mx.kenzie.foundation.assembler.tool.PoolReference;
import org.valross.constantine.RecordConstant;

import java.io.IOException;
import java.io.OutputStream;

import static mx.kenzie.foundation.assembler.constant.ConstantPoolInfo.UTF8;

public record Code(PoolReference attribute_name_index, U4 attribute_length, U2 max_stack, U2 max_locals, U4 code_length,
                   UVec code, U2 exception_table_length,

                   Exception[] exception_table, U2 attributes_count,
                   CodeAttributeInfo... attributes) implements AttributeInfo, Data, UVec, RecordConstant {

    public static Code of(ClassFileBuilder.Helper helper, U2 max_stack, U2 max_locals, UVec code, Exception[] exception_table, CodeAttributeInfo... attributes) {
        final U4 codeLength = U4.valueOf(code.length());
        final U2 exceptionsLength = U2.valueOf(exception_table.length), attributeCount = U2.valueOf(attributes.length);
        final U4 length = new U4(2L + 2 + 4 + codeLength.intValue() + 2 + exceptionsLength.shortValue() + (exception_table.length * 8L) + 2 + U4.lengthOf(attributes).intValue());
        return new Code(helper.constant(UTF8, "Code"), length, max_stack, max_locals, codeLength, code, exceptionsLength, exception_table, attributeCount, attributes);
    }

    @Override
    public UVec info() {
        return UVec.of(max_stack, max_locals, code_length, code, exception_table_length, UVec.of(exception_table), attributes_count, UVec.of(attributes));
    }

    @Override
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        this.attribute_name_index().write(stream);
        this.attribute_length().write(stream);
        this.max_stack.write(stream);
        this.max_locals.write(stream);
        this.code_length.write(stream);
        this.code.write(stream);
        this.exception_table_length.write(stream);
        for (Exception exception : exception_table) exception.write(stream);
        this.attributes_count.write(stream);
        for (AttributeInfo attribute : attributes) attribute.write(stream);
    }

    public record Exception(U2 start_pc, U2 end_pc, U2 handler_pc,
                            U2 catch_type) implements Data, UVec, RecordConstant {

        @Override
        public int length() {
            return 8;
        }

        @Override
        public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
            this.start_pc.write(stream);
            this.end_pc.write(stream);
            this.handler_pc.write(stream);
            this.catch_type.write(stream);
        }

    }

}
