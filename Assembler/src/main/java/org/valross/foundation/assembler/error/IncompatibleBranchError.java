package org.valross.foundation.assembler.error;

import org.valross.foundation.assembler.code.Branch;
import org.valross.foundation.assembler.code.CodeElement;
import org.valross.foundation.assembler.code.CodeVector;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * An error thrown when the program enters a branch in a state that cannot be reconciled with the branch's frame.
 * Two states are considered irreconcilable if:
 * 1. They have a different number of elements on the stack (e.g. {@code [int] != [int, int]}).
 * 2. They have a different number of variables in the register (e.g. {@code [0: int] != [0: int, 1: int]}).
 * 3. They have a different composition of the stack (e.g. {@code [int, float] != [int, int]}). .
 * 4. They have a different composition of the register (e.g. {@code [0: int, 1: float] != [0: int, 1: int]}).
 * As such, this error will only occur if a branch can be entered from more than one place,
 * and each entrypoint has a different frame.
 * The most likely source of this is from a "run-on" branch entry; where a condition jumps to a target branch,
 * but its failure case reaches the same branch with a different stack composition, as seen below:
 * <pre>{@code
 * aload_0, ifnull -> branch, aload_1, branch, ...
 * }</pre>
 * In the above example, the var-0 object is popped by the {@code ifnull} check, so the program will jump to {@code
 * branch} with an empty stack {@code []}. However, after the failure, another object is placed on the stack and this
 * "runs on" into the {@code branch}, with the stack {@code [Object]}.
 * As a result, the branch has two entry-points with different frame compositions that cannot be reconciled.
 * In other words, it is impossible for the compiler/verifier to know the state of the program at the branch,
 * and so the code cannot be verified.
 * There are two approaches to addressing this incompatibility:
 * 1. Sanitise the code so that branches never have more than one entrypoint.
 * This is the simplest way to guarantee the frame will never be incompatible,
 * since there is only one possible composition for the stack.
 * This can be achieved by placing some kind of 'escape' before every branch, such as returning, throwing an error
 * or using an 'always jump' {@link org.valross.foundation.assembler.code.OpCode#GOTO} instruction.
 * 2. Make sure all entry-points have the same composition.
 * This is done by making sure the number and type of all elements on the stack, and the indices and types of all
 * variables are the same at each entrypoint.
 */
public class IncompatibleBranchError extends Error {

    private CodeVector vector;
    private CodeElement element;
    private Branch branch;
    private int index;

    public IncompatibleBranchError() {
        super();
    }

    public IncompatibleBranchError(String message) {
        super(message);
    }

    public IncompatibleBranchError(String message, Throwable cause) {
        super(message, cause);
    }

    public IncompatibleBranchError(Throwable cause) {
        super(cause);
    }

    protected IncompatibleBranchError(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IncompatibleBranchError setVector(CodeVector vector) {
        this.vector = vector;
        return this;
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        if (vector != null) s.println("Current method: " + vector);
        if (vector != null) s.println("Current index: " + index);
        if (branch != null) s.println("Current branch: " + branch);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        if (vector != null) s.println("Current method: " + vector);
        if (vector != null) s.println("Current index: " + index);
        if (branch != null) s.println("Current branch: " + branch);
    }

    public IncompatibleBranchError setProblem(CodeElement element) {
        this.element = element;
        return this;
    }

    public IncompatibleBranchError setIndex(int index) {
        this.index = index;
        return this;
    }

    public CodeVector getVector() {
        return vector;
    }

    public CodeElement getElement() {
        return element;
    }

    public int getIndex() {
        return index;
    }

    public IncompatibleBranchError setBranch(Branch branch) {
        this.branch = branch;
        return this;
    }

    public Branch getBranch() {
        return branch;
    }

}
