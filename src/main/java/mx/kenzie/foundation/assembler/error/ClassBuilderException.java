package mx.kenzie.foundation.assembler.error;

public class ClassBuilderException extends RuntimeException {

    public ClassBuilderException() {
        super();
    }

    public ClassBuilderException(String message) {
        super(message);
    }

    public ClassBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassBuilderException(Throwable cause) {
        super(cause);
    }

    protected ClassBuilderException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
