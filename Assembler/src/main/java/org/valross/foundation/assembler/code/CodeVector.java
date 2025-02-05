package org.valross.foundation.assembler.code;

import org.jetbrains.annotations.NotNull;
import org.valross.foundation.assembler.vector.UVec;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CodeVector implements UVec, Iterable<CodeElement> {

    protected List<CodeElement> code;

    public CodeVector() {
        this.code = new ArrayList<>();
    }

    public void append(@NotNull CodeElement element) {
        this.code.add(element);
    }

    public void append(@NotNull CodeElement @NotNull ... elements) {
        if (elements.length == 0) return;
        this.code.addAll(List.of(elements));
    }

    public void append() {
    }

    public void insert(int index, @NotNull CodeElement... elements) {
        if (elements.length == 0) return;
        this.code.addAll(index, List.of(elements));
    }

    public void insertAfter(@NotNull CodeElement target, @NotNull CodeElement element) {
        final int index = code.indexOf(target);
        if (index >= code.size() - 1) code.add(element);
        else code.add(index + 1, element);
    }

    public void insertBefore(@NotNull CodeElement target, @NotNull CodeElement element) {
        final int index = code.indexOf(target);
        this.code.add(index, element);
    }

    @Override
    public int length() {
        int length = 0;
        for (CodeElement element : code) length += element.length();
        return length;
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        for (CodeElement element : code) element.write(stream);
    }

    @Override
    public ConVec constant() {
        return UVec.of(this.binary());
    }

    @NotNull
    @Override
    public Iterator<CodeElement> iterator() {
        return new Iterator<>() {
            int index;

            @Override
            public boolean hasNext() {
                return index < code.size();
            }

            @Override
            public CodeElement next() {
                return code.get(index++);
            }
        };
    }

    public List<CodeElement> getLast(int instructions) {
        return code.subList(code.size() - instructions, code.size());
    }

    @Override
    public String toString() {
        if (code.isEmpty()) return "[]";
        final StringBuilder builder = new StringBuilder("[");
        boolean first = true;
        int index = 0;
        for (CodeElement element : code) {
            final byte[] binary = element.binary();
            if (first) first = false;
            else builder.append(", ");
            if (binary.length == 0) {
                if (element instanceof Branch)
                    builder.append("<branch> ");
                first = true;
                continue;
            }
            builder.append(index).append(": ");
            boolean opcode = true;
            for (byte b : binary) {
                if (opcode) {
                    opcode = false;
                    builder.append(OpCode.getCode(Byte.toUnsignedInt(b)).toString());
                } else builder.append(" ").append(b);
            }
            if (element instanceof JumpCode.JumpInstruction instruction) {
                builder.append(" (target: ").append(instruction.target().getHandle().index()).append(")");
            }
            index += element.length();
        }
        return builder.append("]").toString();
    }

    public boolean isEmpty() {
        return code.isEmpty();
    }

    public CodeElement getAfter(CodeElement element) {
        final int index = code.indexOf(element);
        if (index == -1 || index >= code.size() - 1) return null;
        return this.code.get(index + 1);
    }

}
