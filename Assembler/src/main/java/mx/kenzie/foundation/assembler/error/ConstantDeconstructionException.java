package mx.kenzie.foundation.assembler.error;

/**
 * Represents a problem that occurs when dismantling a {@link org.valross.constantine.Constant} into
 * its constituent parts to store in the class file constant pool.
 */
public class ConstantDeconstructionException extends RuntimeException {

    public ConstantDeconstructionException() {
        super();
    }

    public ConstantDeconstructionException(String message) {
        super(message);
    }

    public ConstantDeconstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstantDeconstructionException(Throwable cause) {
        super(cause);
    }

    protected ConstantDeconstructionException(String message, Throwable cause, boolean enableSuppression,
                                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
