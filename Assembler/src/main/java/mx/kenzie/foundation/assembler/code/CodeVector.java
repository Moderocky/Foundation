package mx.kenzie.foundation.assembler.code;

import mx.kenzie.foundation.assembler.UVec;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CodeVector implements UVec {

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
        if (index == code.size() - 1) code.add(element);
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
    public void write(OutputStream stream) throws IOException, ReflectiveOperationException {
        for (CodeElement element : code) element.write(stream);
    }

    @Override
    public ConVec constant() {
        return UVec.of(this.binary());
    }

}
