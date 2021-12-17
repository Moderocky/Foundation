package mx.kenzie.foundation;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.objectweb.asm.Opcodes.*;

public interface WriteInstruction extends BiConsumer<CodeWriter, MethodVisitor> {
    
    //region Helper Functions
    static SimpleWriteInstruction throwErrorMessage(final String message) {
        return method -> {
            method.visitTypeInsn(NEW, new Type(RuntimeException.class).internalName());
            method.visitInsn(DUP);
            method.visitLdcInsn(message);
            method.visitMethodInsn(INVOKESPECIAL, new Type(RuntimeException.class).internalName(), "<init>", "(Ljava/lang/String;)V", false);
            method.visitInsn(ATHROW);
        };
    }
    
    static SimpleWriteInstruction newInstance(final Type type, final Constructor<?> constructor) {
        if (constructor.getParameterTypes().length > 0)
            throw new IllegalArgumentException("Constructor cannot have parameter types.");
        return method -> {
            method.visitTypeInsn(NEW, type.internalName());
            method.visitInsn(DUP);
            method.visitMethodInsn(INVOKESPECIAL, new Type(constructor.getDeclaringClass()).internalName(), "<init>", "()V", false);
        };
    }
    
    static SimpleWriteInstruction println(final String string) {
        return method -> {
            method.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            method.visitLdcInsn(string);
            method.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        };
    }
    
    static SimpleWriteInstruction printTopSmall() {
        return method -> {
            method.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            method.visitInsn(SWAP);
            method.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        };
    }
    
    static SimpleWriteInstruction printTopObject() {
        return method -> {
            method.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            method.visitInsn(SWAP);
            method.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
        };
    }
    
    static SimpleWriteInstruction assertTrue() {
        return method -> {
            final Label label = new Label();
            method.visitJumpInsn(IFNE, label);
            method.visitTypeInsn(NEW, "java/lang/AssertionError");
            method.visitInsn(DUP);
            method.visitMethodInsn(INVOKESPECIAL, "java/lang/AssertionError", "<init>", "()V", false);
            method.visitInsn(ATHROW);
            method.visitLabel(label);
        };
    }
    
    static SimpleWriteInstruction noOp() {
        return method -> method.visitInsn(NOP);
    }
    
    static SimpleWriteInstruction implementationDependent1() {
        return method -> method.visitInsn(254);
    }
    
    static SimpleWriteInstruction implementationDependent2() {
        return method -> method.visitInsn(255);
    }
    //endregion
    
    //region Frames
    static SimpleWriteInstruction fullFrame(final int numLocals, final Object[] locals, final int numStack, final Object[] stack) {
        return method -> method.visitFrame(F_FULL, numLocals, locals, numStack, stack);
    }
    
    static SimpleWriteInstruction fullFrame(final Object[] locals, final Object[] stack) {
        return method -> method.visitFrame(F_FULL, locals.length, locals, stack.length, stack);
    }
    
    static SimpleWriteInstruction sameFrame() {
        return method -> method.visitFrame(F_SAME, 0, new Object[0], 0, new Object[0]);
    }
    
    static SimpleWriteInstruction sameFrame(final Object[] locals, final Object[] stack) {
        return method -> method.visitFrame(F_SAME, locals.length, locals, stack.length, stack);
    }
    
    static SimpleWriteInstruction sameFrameSingleStack(final Object[] locals, final Object[] stack) {
        return method -> method.visitFrame(F_SAME1, locals.length, locals, stack.length, stack);
    }
    
    static SimpleWriteInstruction appendFrame(final Object[] locals, final Object[] stack) {
        return method -> method.visitFrame(F_APPEND, locals.length, locals, stack.length, stack);
    }
    
    static SimpleWriteInstruction chopFrame(final Object[] locals, final Object[] stack) {
        return method -> method.visitFrame(F_CHOP, locals.length, locals, stack.length, stack);
    }
    
    static SimpleWriteInstruction newFrame(final Object[] locals, final Object[] stack) {
        return method -> method.visitFrame(F_NEW, locals.length, locals, stack.length, stack);
    }
    //endregion
    
    //region Exceptions
    static WriteInstruction tryCatch(final Type exception, final String tryStart, final String tryEnd, final String catchStart) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(tryStart, new Label());
            writer.labels.putIfAbsent(tryEnd, new Label());
            writer.labels.putIfAbsent(catchStart, new Label());
            final Label a = writer.labels.get(tryStart),
                b = writer.labels.get(tryEnd),
                c = writer.labels.get(catchStart);
            method.visitTryCatchBlock(a, b, c, exception.internalName());
        };
    }
    
    static WriteInstruction tryFinally(final String tryStart, final String tryEnd, final String finallyStart) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(tryStart, new Label());
            writer.labels.putIfAbsent(tryEnd, new Label());
            writer.labels.putIfAbsent(finallyStart, new Label());
            final Label a = writer.labels.get(tryStart),
                b = writer.labels.get(tryEnd),
                c = writer.labels.get(finallyStart);
            method.visitTryCatchBlock(a, b, c, null);
        };
    }
    
    //region Try/Catch Builder
    static TryExpectFinally trySection(final WriteInstruction... instructions) {
        return new TryExpectFinally(null, instructions);
    }
    
    static TryExpectCatch trySection(final Type exception, final WriteInstruction... instructions) {
        return new TryExpectCatch(exception.internalName(), instructions);
    }
    
    class TryExpectFinally extends TryExpectCatch {
        TryExpectFinally(String exception, WriteInstruction... instructions) {
            super(exception, instructions);
        }
        
        public WriteInstruction finallySection(final WriteInstruction... instructions) {
            return (writer, method) -> {
                final Label a = new Label(), b = new Label(), c = new Label();
                method.visitTryCatchBlock(a, b, c, exception);
                method.visitLabel(a);
                for (final WriteInstruction instruction : trySection) {
                    instruction.accept(writer, method);
                }
                method.visitLabel(b);
                method.visitLabel(c);
                for (final WriteInstruction instruction : instructions) {
                    instruction.accept(writer, method);
                }
            };
        }
        
        
    }
    
    class TryExpectCatch {
        protected final List<WriteInstruction> trySection = new ArrayList<>();
        protected final String exception;
        
        TryExpectCatch(final String exception, final WriteInstruction... instructions) {
            this.trySection.addAll(List.of(instructions));
            this.exception = exception;
        }
        
        public WriteInstruction catchSection(final WriteInstruction... instructions) {
            return (writer, method) -> {
                final Label a = new Label(), b = new Label(), c = new Label(), exit = new Label();
                method.visitTryCatchBlock(a, b, c, exception);
                method.visitLabel(a);
                for (final WriteInstruction instruction : trySection) {
                    instruction.accept(writer, method);
                }
                method.visitLabel(b);
                method.visitJumpInsn(GOTO, exit);
                method.visitLabel(c);
                for (final WriteInstruction instruction : instructions) {
                    instruction.accept(writer, method);
                }
                method.visitLabel(exit);
            };
        }
        
    }
    //endregion
    //endregion
    
    //region Arrays
    static SimpleWriteInstruction newMultiArray(final Type type, final int dimensions) {
        return method -> {
            method.visitMultiANewArrayInsn(type.internalName(), dimensions);
        };
    }
    
    static SimpleWriteInstruction newArray(final Type type) {
        return method -> method.visitTypeInsn(ANEWARRAY, type.internalName());
    }
    
    static SimpleWriteInstruction newArray(final Type type, final int length) {
        return method -> {
            method.visitIntInsn(BIPUSH, length);
            method.visitTypeInsn(ANEWARRAY, type.internalName());
        };
    }
    
    static SimpleWriteInstruction newArray(final Class<?> type) {
        final int ensign;
        if (type == byte.class) ensign = T_BYTE;
        else if (type == int.class) ensign = T_INT;
        else if (type == short.class) ensign = T_SHORT;
        else if (type == long.class) ensign = T_LONG;
        else if (type == float.class) ensign = T_FLOAT;
        else if (type == double.class) ensign = T_DOUBLE;
        else if (type == char.class) ensign = T_CHAR;
        else if (type == boolean.class) ensign = T_BOOLEAN;
        else ensign = -1;
        if (ensign > -1) return method -> method.visitIntInsn(NEWARRAY, ensign);
        else return method -> method.visitTypeInsn(ANEWARRAY, new Type(type).internalName());
    }
    
    static SimpleWriteInstruction newArray(final Class<?> type, final int length) {
        final int ensign;
        if (type == byte.class) ensign = T_BYTE;
        else if (type == int.class) ensign = T_INT;
        else if (type == short.class) ensign = T_SHORT;
        else if (type == long.class) ensign = T_LONG;
        else if (type == float.class) ensign = T_FLOAT;
        else if (type == double.class) ensign = T_DOUBLE;
        else if (type == char.class) ensign = T_CHAR;
        else if (type == boolean.class) ensign = T_BOOLEAN;
        else ensign = -1;
        if (ensign > -1) return method -> {
            method.visitIntInsn(BIPUSH, length);
            method.visitIntInsn(NEWARRAY, ensign);
        };
        else return method -> {
            method.visitIntInsn(BIPUSH, length);
            method.visitTypeInsn(ANEWARRAY, new Type(type).internalName());
        };
    }
    
    static SimpleWriteInstruction arrayStoreObject() {
        return method -> method.visitInsn(AASTORE);
    }
    
    static SimpleWriteInstruction arrayStoreObject(final int index) {
        return method -> {
            method.visitIntInsn(BIPUSH, index);
            method.visitInsn(SWAP);
            method.visitInsn(AASTORE);
        };
    }
    
    static SimpleWriteInstruction arrayLoadObject() {
        return method -> method.visitInsn(AALOAD);
    }
    
    static SimpleWriteInstruction arrayLoadObject(final int index) {
        return method -> {
            method.visitIntInsn(BIPUSH, index);
            method.visitInsn(AALOAD);
        };
    }
    
    static SimpleWriteInstruction arrayStore(final Class<?> type) {
        final int ensign;
        if (type == byte.class) ensign = BASTORE;
        else if (type == int.class) ensign = IASTORE;
        else if (type == short.class) ensign = SASTORE;
        else if (type == long.class) ensign = LASTORE;
        else if (type == float.class) ensign = FASTORE;
        else if (type == double.class) ensign = DASTORE;
        else if (type == char.class) ensign = CASTORE;
        else if (type == boolean.class) ensign = BASTORE;
        else ensign = AASTORE;
        return method -> method.visitInsn(ensign);
    }
    
    static SimpleWriteInstruction arrayLoad(final Class<?> type) {
        final int ensign;
        if (type == byte.class) ensign = BALOAD;
        else if (type == int.class) ensign = IALOAD;
        else if (type == short.class) ensign = SALOAD;
        else if (type == long.class) ensign = LALOAD;
        else if (type == float.class) ensign = FALOAD;
        else if (type == double.class) ensign = DALOAD;
        else if (type == char.class) ensign = CALOAD;
        else if (type == boolean.class) ensign = BALOAD;
        else ensign = AALOAD;
        return method -> method.visitInsn(ensign);
    }
    
    static SimpleWriteInstruction arrayStore(final Class<?> type, final int index) {
        final int ensign;
        if (type == byte.class) ensign = BASTORE;
        else if (type == int.class) ensign = IASTORE;
        else if (type == short.class) ensign = SASTORE;
        else if (type == long.class) ensign = LASTORE;
        else if (type == float.class) ensign = FASTORE;
        else if (type == double.class) ensign = DASTORE;
        else if (type == char.class) ensign = CASTORE;
        else if (type == boolean.class) ensign = BASTORE;
        else ensign = AASTORE;
        return method -> {
            method.visitIntInsn(BIPUSH, index);
            method.visitInsn(SWAP);
            method.visitInsn(ensign);
        };
    }
    
    static SimpleWriteInstruction arrayLoad(final Class<?> type, final int index) {
        final int ensign;
        if (type == byte.class) ensign = BALOAD;
        else if (type == int.class) ensign = IALOAD;
        else if (type == short.class) ensign = SALOAD;
        else if (type == long.class) ensign = LALOAD;
        else if (type == float.class) ensign = FALOAD;
        else if (type == double.class) ensign = DALOAD;
        else if (type == char.class) ensign = CALOAD;
        else if (type == boolean.class) ensign = BALOAD;
        else ensign = AALOAD;
        return method -> {
            method.visitIntInsn(BIPUSH, index);
            method.visitInsn(ensign);
        };
    }
    
    static SimpleWriteInstruction arrayLength() {
        return method -> method.visitInsn(ARRAYLENGTH);
    }
    //endregion
    
    //region Stack Manipulation
    static SimpleWriteInstruction cast(final Type type) {
        return method -> method.visitTypeInsn(CHECKCAST, type.internalName());
    }
    
    static SimpleWriteInstruction pop() {
        return method -> method.visitInsn(POP);
    }
    
    static SimpleWriteInstruction pop2() {
        return method -> method.visitInsn(POP2);
    }
    
    static SimpleWriteInstruction swap() {
        return method -> method.visitInsn(SWAP);
    }
    
    static SimpleWriteInstruction duplicate() {
        return method -> method.visitInsn(DUP);
    }
    
    static SimpleWriteInstruction duplicateDrop2() {
        return method -> method.visitInsn(DUP_X1);
    }
    
    static SimpleWriteInstruction duplicateDrop3() {
        return method -> method.visitInsn(DUP_X2);
    }
    
    static SimpleWriteInstruction duplicate2() {
        return method -> method.visitInsn(DUP2);
    }
    
    static SimpleWriteInstruction duplicate2Drop2() {
        return method -> method.visitInsn(DUP2_X1);
    }
    
    static SimpleWriteInstruction duplicate2Drop3() {
        return method -> method.visitInsn(DUP2_X2);
    }
    
    static SimpleWriteInstruction allocate(final Type type) {
        return method -> method.visitTypeInsn(NEW, type.internalName());
    }
    
    static SimpleWriteInstruction allocate(final Class<?> type) {
        return allocate(new Type(type));
    }
    
    static SimpleWriteInstruction instanceOf(final Type type) {
        return method -> method.visitTypeInsn(INSTANCEOF, type.internalName());
    }
    
    static SimpleWriteInstruction lockMonitor() {
        return method -> method.visitInsn(MONITORENTER);
    }
    
    static SimpleWriteInstruction unlockMonitor() {
        return method -> method.visitInsn(MONITOREXIT);
    }
    //endregion
    
    //region Jumps
    static WriteInstruction label(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitLabel(to);
        };
    }
    
    static WriteInstruction jump(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(GOTO, to);
        };
    }
    
    static WriteInstruction jumpSubroutine(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(JSR, to);
        };
    }
    
    static WriteInstruction jumpWideSubroutine(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(201, to);
        };
    }
    
    //region Object Comparisons
    static WriteInstruction jumpIfEquals(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IF_ACMPEQ, to);
        };
    }
    
    static WriteInstruction jumpIfNotEquals(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IF_ACMPNE, to);
        };
    }
    
    static WriteInstruction jumpIfNull(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IFNULL, to);
        };
    }
    
    static WriteInstruction jumpIfInstanceOf(final Type type, final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitTypeInsn(INSTANCEOF, type.internalName());
            method.visitJumpInsn(IFNE, to);
        };
    }
    
    static WriteInstruction jumpIfNotNull(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IFNONNULL, to);
        };
    }
    //endregion
    
    //region Zero Comparisons
    static WriteInstruction jumpIfTrue(final String label) {
        return jumpIfNot0(label);
    }
    
    static WriteInstruction jumpIfFalse(final String label) {
        return jumpIf0(label);
    }
    
    static WriteInstruction jumpIf0(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IFEQ, to);
        };
    }
    
    static WriteInstruction jumpIfNot0(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IFNE, to);
        };
    }
    
    static WriteInstruction jumpIfGT0(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IFGT, to);
        };
    }
    
    static WriteInstruction jumpIfLT0(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IFLT, to);
        };
    }
    
    static WriteInstruction jumpIfGTEQ0(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IFGE, to);
        };
    }
    
    static WriteInstruction jumpIfLTEQ0(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IFLE, to);
        };
    }
    //endregion
    
    //region Number Comparisons
    static WriteInstruction jumpIfEQ(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IF_ICMPEQ, to);
        };
    }
    
    static WriteInstruction jumpIfNE(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IF_ICMPNE, to);
        };
    }
    
    static WriteInstruction jumpIfGT(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IF_ICMPGT, to);
        };
    }
    
    static WriteInstruction jumpIfGTEQ(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IF_ICMPGE, to);
        };
    }
    
    static WriteInstruction jumpIfLT(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IF_ICMPLT, to);
        };
    }
    
    static WriteInstruction jumpIfLTEQ(final String label) {
        return (writer, method) -> {
            writer.labels.putIfAbsent(label, new Label());
            final Label to = writer.labels.get(label);
            method.visitJumpInsn(IF_ICMPLE, to);
        };
    }
    
    static WriteInstruction jumpIf(final String comparison, final String label) {
        switch (comparison) {
            case "=":
            case "==":
                return jumpIfEQ(label);
            case "!=":
                return jumpIfNE(label);
            case ">":
                return jumpIfGT(label);
            case "<":
                return jumpIfLT(label);
            case ">=":
                return jumpIfGTEQ(label);
            case "<=":
                return jumpIfLTEQ(label);
        }
        throw new IllegalArgumentException("Cannot match '" + comparison + "' to instruction.");
    }
    
    static WriteInstruction jumpIfAnyEquals(final String label, final Type type) {
        return switch (type.dotPath()) {
            case "int", "byte", "boolean", "short", "char" -> jumpIfEQ(label);
            case "long" -> (writer, method) -> {
                writer.labels.putIfAbsent(label, new Label());
                final Label to = writer.labels.get(label);
                method.visitInsn(LCMP);
                method.visitJumpInsn(IFEQ, to);
            };
            case "float" -> (writer, method) -> {
                writer.labels.putIfAbsent(label, new Label());
                final Label to = writer.labels.get(label);
                method.visitInsn(FCMPL);
                method.visitJumpInsn(IFEQ, to);
            };
            case "double" -> (writer, method) -> {
                writer.labels.putIfAbsent(label, new Label());
                final Label to = writer.labels.get(label);
                method.visitInsn(DCMPL);
                method.visitJumpInsn(IFEQ, to);
            };
            default -> jumpIfEquals(label);
        };
    }
    
    static WriteInstruction jumpIfAnyNotEquals(final String label, final Type type) {
        return switch (type.dotPath()) {
            case "int", "byte", "boolean", "short", "char" -> jumpIfEQ(label);
            case "long" -> (writer, method) -> {
                writer.labels.putIfAbsent(label, new Label());
                final Label to = writer.labels.get(label);
                method.visitInsn(LCMP);
                method.visitJumpInsn(IFNE, to);
            };
            case "float" -> (writer, method) -> {
                writer.labels.putIfAbsent(label, new Label());
                final Label to = writer.labels.get(label);
                method.visitInsn(FCMPL);
                method.visitJumpInsn(IFNE, to);
            };
            case "double" -> (writer, method) -> {
                writer.labels.putIfAbsent(label, new Label());
                final Label to = writer.labels.get(label);
                method.visitInsn(DCMPL);
                method.visitJumpInsn(IFNE, to);
            };
            default -> jumpIfEquals(label);
        };
    }
    //endregion
    //endregion
    
    //region Constants
    static SimpleWriteInstruction loadClassConstant(final Type value) {
        return method -> method.visitLdcInsn(org.objectweb.asm.Type.getObjectType(value.internalName()));
    }
    
    static SimpleWriteInstruction loadConstant(final Object value) {
        if (value instanceof Type) return WriteInstruction.loadClassConstant((Type) value);
        return method -> method.visitLdcInsn(value);
    }
    
    static SimpleWriteInstruction loadConstant(final Method method) {
        return m -> m.visitLdcInsn(AccessUtility.getHandle(method));
    }
    
    static SimpleWriteInstruction loadConstant(final Constructor<?> constructor) {
        return m -> m.visitLdcInsn(AccessUtility.getHandle(constructor));
    }
    
    static SimpleWriteInstruction pushNull() {
        return method -> method.visitInsn(ACONST_NULL);
    }
    
    static SimpleWriteInstruction push0() {
        return method -> method.visitInsn(ICONST_0);
    }
    
    static SimpleWriteInstruction push1() {
        return method -> method.visitInsn(ICONST_1);
    }
    
    static SimpleWriteInstruction push(boolean z) {
        return method -> method.visitInsn(z ? ICONST_1 : ICONST_0);
    }
    
    static SimpleWriteInstruction push(byte number) {
        return method -> method.visitIntInsn(BIPUSH, number);
    }
    
    static SimpleWriteInstruction push(short number) {
        return method -> method.visitIntInsn(SIPUSH, number);
    }
    
    static SimpleWriteInstruction push(int number) {
        return loadConstant(number);
    }
    
    static SimpleWriteInstruction push(final String value) {
        return loadConstant(value);
    }
    //endregion
    
    //region Arithmetic
    //region Add
    static SimpleWriteInstruction incrementSmall(final int variable, final int amount) {
        return method -> method.visitIincInsn(variable, amount);
    }
    
    static SimpleWriteInstruction addSmall() {
        return method -> method.visitInsn(IADD);
    }
    
    static SimpleWriteInstruction addLong() {
        return method -> method.visitInsn(LADD);
    }
    
    static SimpleWriteInstruction addFloat() {
        return method -> method.visitInsn(FADD);
    }
    
    static SimpleWriteInstruction addDouble() {
        return method -> method.visitInsn(DADD);
    }
    //endregion
    
    //region Subtract
    static SimpleWriteInstruction subtractSmall() {
        return method -> method.visitInsn(ISUB);
    }
    
    static SimpleWriteInstruction subtractLong() {
        return method -> method.visitInsn(LSUB);
    }
    
    static SimpleWriteInstruction subtractFloat() {
        return method -> method.visitInsn(FSUB);
    }
    
    static SimpleWriteInstruction subtractDouble() {
        return method -> method.visitInsn(DSUB);
    }
    //endregion
    
    //region Multiply
    static SimpleWriteInstruction multiplySmall() {
        return method -> method.visitInsn(IMUL);
    }
    
    static SimpleWriteInstruction multiplyLong() {
        return method -> method.visitInsn(LMUL);
    }
    
    static SimpleWriteInstruction multiplyFloat() {
        return method -> method.visitInsn(FMUL);
    }
    
    static SimpleWriteInstruction multiplyDouble() {
        return method -> method.visitInsn(DMUL);
    }
    //endregion
    
    //region Divide
    static SimpleWriteInstruction divideSmall() {
        return method -> method.visitInsn(IDIV);
    }
    
    static SimpleWriteInstruction divideLong() {
        return method -> method.visitInsn(LDIV);
    }
    
    static SimpleWriteInstruction divideFloat() {
        return method -> method.visitInsn(FDIV);
    }
    
    static SimpleWriteInstruction divideDouble() {
        return method -> method.visitInsn(DDIV);
    }
    //endregion
    
    //region Negate
    static SimpleWriteInstruction negateSmall() {
        return method -> method.visitInsn(INEG);
    }
    
    static SimpleWriteInstruction negateLong() {
        return method -> method.visitInsn(LNEG);
    }
    
    static SimpleWriteInstruction negateFloat() {
        return method -> method.visitInsn(FNEG);
    }
    
    static SimpleWriteInstruction negateDouble() {
        return method -> method.visitInsn(DNEG);
    }
    //endregion
    //endregion
    
    //region Bit Manipulation
    //region Shift
    static SimpleWriteInstruction leftShiftSmall() {
        return method -> method.visitInsn(ISHL);
    }
    
    static SimpleWriteInstruction leftShiftLong() {
        return method -> method.visitInsn(LSHL);
    }
    
    static SimpleWriteInstruction rightShiftSmall() {
        return method -> method.visitInsn(ISHR);
    }
    
    static SimpleWriteInstruction rightShiftLong() {
        return method -> method.visitInsn(LSHR);
    }
    
    static SimpleWriteInstruction uShiftSmall() {
        return method -> method.visitInsn(IUSHR);
    }
    
    static SimpleWriteInstruction uShiftLong() {
        return method -> method.visitInsn(LUSHR);
    }
    //endregion
    
    //region Operations
    static SimpleWriteInstruction andSmall() {
        return method -> method.visitInsn(IAND);
    }
    
    static SimpleWriteInstruction andLong() {
        return method -> method.visitInsn(LAND);
    }
    
    static SimpleWriteInstruction orSmall() {
        return method -> method.visitInsn(IOR);
    }
    
    static SimpleWriteInstruction orLong() {
        return method -> method.visitInsn(LOR);
    }
    
    static SimpleWriteInstruction xorSmall() {
        return method -> method.visitInsn(IXOR);
    }
    
    static SimpleWriteInstruction xorLong() {
        return method -> method.visitInsn(LXOR);
    }
    
    static SimpleWriteInstruction flipSmall() {
        return method -> {
            method.visitInsn(ICONST_M1);
            method.visitInsn(IXOR);
        };
    }
    
    static SimpleWriteInstruction flipLong() {
        return method -> {
            method.visitInsn(ICONST_M1);
            method.visitInsn(LXOR);
        };
    }
    //endregion
    //endregion
    
    //region Number Conversion
    static SimpleWriteInstruction convert(final Class<?> from, final Class<?> to) {
        if (!from.isPrimitive() && !to.isPrimitive()) return cast(new Type(to));
        final int opcode;
        if (from == float.class) {
            if (to == double.class) opcode = F2D;
            else if (to == long.class) opcode = F2L;
            else opcode = F2I;
        } else if (from == double.class) {
            if (to == float.class) opcode = D2F;
            else if (to == long.class) opcode = D2L;
            else opcode = D2I;
        } else if (from == long.class) {
            if (to == float.class) opcode = L2F;
            else if (to == double.class) opcode = L2D;
            else opcode = L2I;
        } else {
            if (to == float.class) opcode = I2F;
            else if (to == double.class) opcode = I2D;
            else if (to == byte.class) opcode = I2B;
            else if (to == short.class) opcode = I2S;
            else if (to == char.class) opcode = I2C;
            else opcode = I2L;
        }
        return method -> method.visitInsn(opcode);
    }
    
    static SimpleWriteInstruction convert(final Type from, final Type to) {
        if (!from.isPrimitive() && !to.isPrimitive()) return cast(to);
        final int opcode = switch (from.dotPath()) {
            case "float" -> switch (to.dotPath()) {
                case "double" -> F2D;
                case "long" -> F2L;
                default -> F2I;
            };
            case "double" -> switch (to.dotPath()) {
                case "float" -> D2F;
                case "long" -> D2L;
                default -> D2I;
            };
            case "long" -> switch (to.dotPath()) {
                case "float" -> L2F;
                case "double" -> L2D;
                default -> L2I;
            };
            default -> switch (to.dotPath()) {
                case "float" -> I2F;
                case "double" -> I2D;
                case "byte" -> I2B;
                case "short" -> I2S;
                case "char" -> I2C;
                default -> I2L;
            };
        };
        return method -> method.visitInsn(opcode);
    }
    //endregion
    
    //region Field Access
    static SimpleWriteInstruction setStaticField(final Field field) {
        return setStaticField(new Type(field.getDeclaringClass()), new FieldErasure(field));
    }
    
    static SimpleWriteInstruction setStaticField(final Type owner, final FieldErasure erasure) {
        return method -> method.visitFieldInsn(PUTSTATIC, owner.internalName(), erasure.name(), erasure.type().descriptorString());
    }
    
    static SimpleWriteInstruction setStaticField(final Type owner, final Type type, final String name) {
        return method -> method.visitFieldInsn(PUTSTATIC, owner.internalName(), name, type.descriptorString());
    }
    
    static SimpleWriteInstruction setField(final Type owner, final FieldErasure erasure) {
        return method -> method.visitFieldInsn(PUTFIELD, owner.internalName(), erasure.name(), erasure.type().descriptorString());
    }
    
    static SimpleWriteInstruction setField(final Type owner, final Type type, final String name) {
        return method -> method.visitFieldInsn(PUTFIELD, owner.internalName(), name, type.descriptorString());
    }
    static SimpleWriteInstruction getStaticField(final Field field) {
        return setStaticField(new Type(field.getDeclaringClass()), new FieldErasure(field));
    }
    
    static SimpleWriteInstruction getStaticField(final Type owner, final FieldErasure erasure) {
        return method -> method.visitFieldInsn(GETSTATIC, owner.internalName(), erasure.name(), erasure.type().descriptorString());
    }
    
    static SimpleWriteInstruction getStaticField(final Type owner, final Type type, final String name) {
        return method -> method.visitFieldInsn(GETSTATIC, owner.internalName(), name, type.descriptorString());
    }
    
    static SimpleWriteInstruction getField(final Type owner, final FieldErasure erasure) {
        return method -> method.visitFieldInsn(GETFIELD, owner.internalName(), erasure.name(), erasure.type().descriptorString());
    }
    
    static SimpleWriteInstruction getField(final Type owner, final Type type, final String name) {
        return method -> method.visitFieldInsn(GETFIELD, owner.internalName(), name, type.descriptorString());
    }
    
    static SimpleWriteInstruction setFieldDynamic(final Type returnType, final String name, final Type[] parameterTypes, final Handle handle, final Object... parameters) {
        return method -> method.visitInvokeDynamicInsn(name, AccessUtility.getDescriptor(returnType, parameterTypes), handle, parameters);
    }
    
    static SimpleWriteInstruction getField(final Field field) {
        if (Modifier.isStatic(field.getModifiers()))
            return getStaticField(new Type(field.getDeclaringClass()), new Type(field.getType()), field.getName());
        return getField(new Type(field.getDeclaringClass()), new Type(field.getType()), field.getName());
    }
    
    static SimpleWriteInstruction setField(final Field field) {
        if (Modifier.isStatic(field.getModifiers()))
            return setStaticField(new Type(field.getDeclaringClass()), new Type(field.getType()), field.getName());
        return setField(new Type(field.getDeclaringClass()), new Type(field.getType()), field.getName());
    }
    //endregion
    
    //region Method Invocation
    //region Invoke Virtual
    static SimpleWriteInstruction invokeVirtual(boolean isInterface, final Type owner, final Type returnType, final String name, final Type... parameterTypes) {
        return method -> method.visitMethodInsn(INVOKEVIRTUAL, owner.internalName(), name, AccessUtility.getDescriptor(returnType, parameterTypes), isInterface);
    }
    
    static SimpleWriteInstruction invokeVirtual(final Type owner, final Type returnType, final String name, final Type... parameterTypes) {
        return invokeVirtual(false, owner, returnType, name, parameterTypes);
    }
    
    static SimpleWriteInstruction invokeVirtual(final Type owner, final MethodErasure method) {
        return invokeVirtual(false, owner, method.returnType(), method.name(), method.parameterTypes());
    }
    
    static SimpleWriteInstruction invokeVirtual(final Method method) {
        return invokeVirtual(Modifier.isInterface(method.getDeclaringClass()
            .getModifiers()), new Type(method.getDeclaringClass()), new Type(method.getReturnType()), method.getName(), Type.of(method.getParameterTypes()));
    }
    //endregion
    
    //region Invoke Interface
    static SimpleWriteInstruction invokeInterface(final Type owner, final Type returnType, final String name, final Type... parameterTypes) {
        return method -> method.visitMethodInsn(INVOKEINTERFACE, owner.internalName(), name, AccessUtility.getDescriptor(returnType, parameterTypes), true);
    }
    
    static SimpleWriteInstruction invokeInterface(final Method method) {
        return invokeInterface(new Type(method.getDeclaringClass()), new Type(method.getReturnType()), method.getName(), Type.of(method.getParameterTypes()));
    }
    //endregion
    
    //region Invoke Static
    static SimpleWriteInstruction invokeStatic(boolean isInterface, final Type owner, final Type returnType, final String name, final Type... parameterTypes) {
        return method -> method.visitMethodInsn(INVOKESTATIC, owner.internalName(), name, AccessUtility.getDescriptor(returnType, parameterTypes), isInterface);
    }
    
    static SimpleWriteInstruction invokeStatic(final Type owner, final Type returnType, final String name, final Type... parameterTypes) {
        return invokeStatic(false, owner, returnType, name, parameterTypes);
    }
    
    static SimpleWriteInstruction invokeStatic(final Type owner, final MethodErasure method) {
        return invokeStatic(false, owner, method.returnType(), method.name(), method.parameterTypes());
    }
    
    static SimpleWriteInstruction invokeStatic(final Method method) {
        return invokeStatic(Modifier.isInterface(method.getDeclaringClass()
            .getModifiers()), new Type(method.getDeclaringClass()), new Type(method.getReturnType()), method.getName(), Type.of(method.getParameterTypes()));
    }
    //endregion
    
    //region Invoke Dynamic
    static SimpleWriteInstruction invokeDynamic(final Type returnType, final String name, final Type[] parameterTypes, final Handle handle, final Object... parameters) {
        return method -> method.visitInvokeDynamicInsn(name, AccessUtility.getDescriptor(returnType, parameterTypes), handle, parameters);
    }
    
    static SimpleWriteInstruction invokeDynamic(final Method target, final Method bootstrap, final Object... parameters) {
        if (Modifier.isStatic(target.getModifiers()))
            return method -> method.visitInvokeDynamicInsn(target.getName(), AccessUtility.getDescriptor(new Type(target.getReturnType()), Type.of(target.getParameterTypes())), AccessUtility.getHandle(bootstrap), parameters);
        else {
            int length = target.getParameterTypes().length;
            Class<?>[] ps = target.getParameterTypes();
            Type[] params = new Type[length+1];
            for (int i = 0; i < length; i++) {
                params[i+1] = new Type(ps[i]);
            }
            params[0] = new Type(target.getDeclaringClass());
            return method -> method.visitInvokeDynamicInsn(target.getName(), AccessUtility.getDescriptor(new Type(target.getReturnType()), params), AccessUtility.getHandle(bootstrap), parameters);
        }
    }
    
    //endregion
    
    //region Invoke Special
    static SimpleWriteInstruction invokeSpecial(boolean isInterface, final Type owner, final Type returnType, final String name, final Type... parameterTypes) {
        return method -> method.visitMethodInsn(INVOKESPECIAL, owner.internalName(), name, AccessUtility.getDescriptor(returnType, parameterTypes), isInterface);
    }
    
    static SimpleWriteInstruction invokeSpecial(final Type owner, final Type... parameterTypes) {
        return method -> method.visitMethodInsn(INVOKESPECIAL, owner.internalName(), "<init>", AccessUtility.getDescriptor(new Type(void.class), parameterTypes), false);
    }
    
    static SimpleWriteInstruction invokeSpecial(final Constructor<?> constructor) {
        return invokeSpecial(new Type(constructor.getDeclaringClass()), Type.of(constructor.getParameterTypes()));
    }
    
    static SimpleWriteInstruction superObject() {
        return method -> method.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    }
    //endregion
    
    static SimpleWriteInstruction invoke(final Method method) {
        final int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers)) return invokeStatic(method);
        else if (Modifier.isInterface(method.getDeclaringClass().getModifiers())) return invokeInterface(method);
        return invokeVirtual(method);
    }
    //endregion
    
    //region Variables
    //region Store Variable
    static SimpleWriteInstruction storeObject(int index) {
        return method -> method.visitVarInsn(ASTORE, index);
    }
    
    static SimpleWriteInstruction storeSmall(int index) {
        return method -> method.visitVarInsn(ISTORE, index);
    }
    
    static SimpleWriteInstruction storeLong(int index) {
        return method -> method.visitVarInsn(LSTORE, index);
    }
    
    static SimpleWriteInstruction storeFloat(int index) {
        return method -> method.visitVarInsn(FSTORE, index);
    }
    
    static SimpleWriteInstruction storeDouble(int index) {
        return method -> method.visitVarInsn(DSTORE, index);
    }
    
    static SimpleWriteInstruction store(final Type type, int index) {
        return method -> method.visitVarInsn(type.getStoreOpcode(), index);
    }
    //endregion
    
    //region Load Variable
    static SimpleWriteInstruction loadThis() {
        return method -> method.visitVarInsn(ALOAD, 0);
    }
    
    static SimpleWriteInstruction loadObject(int index) {
        return method -> method.visitVarInsn(ALOAD, index);
    }
    
    static SimpleWriteInstruction loadSmall(int index) {
        return method -> method.visitVarInsn(ILOAD, index);
    }
    
    static SimpleWriteInstruction loadLong(int index) {
        return method -> method.visitVarInsn(LLOAD, index);
    }
    
    static SimpleWriteInstruction loadFloat(int index) {
        return method -> method.visitVarInsn(FLOAD, index);
    }
    
    static SimpleWriteInstruction loadDouble(int index) {
        return method -> method.visitVarInsn(DLOAD, index);
    }
    
    static SimpleWriteInstruction load(final Type type, int index) {
        return method -> method.visitVarInsn(type.getLoadOpcode(), index);
    }
    
    static SimpleWriteInstruction exitSubroutine(int index) {
        return method -> method.visitVarInsn(RET, index);
    }
    //endregion
    //endregion
    
    //region Return
    static SimpleWriteInstruction returnEmpty() {
        return method -> method.visitInsn(RETURN);
    }
    
    static SimpleWriteInstruction returnObject() {
        return method -> method.visitInsn(ARETURN);
    }
    
    static SimpleWriteInstruction returnSmall() {
        return method -> method.visitInsn(IRETURN);
    }
    
    static SimpleWriteInstruction returnLong() {
        return method -> method.visitInsn(LRETURN);
    }
    
    static SimpleWriteInstruction returnFloat() {
        return method -> method.visitInsn(FRETURN);
    }
    
    static SimpleWriteInstruction returnDouble() {
        return method -> method.visitInsn(DRETURN);
    }
    
    static SimpleWriteInstruction throwException() {
        return method -> method.visitInsn(ATHROW);
    }
    //endregion
    
}
