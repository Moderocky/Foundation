package mx.kenzie.foundation.error;

public class PropertyCalculationError extends Error {
    public PropertyCalculationError() {
        super();
    }
    
    public PropertyCalculationError(String message) {
        super(message);
    }
    
    public PropertyCalculationError(String message, Throwable cause) {
        super(message, cause);
    }
    
    public PropertyCalculationError(Throwable cause) {
        super(cause);
    }
    
    protected PropertyCalculationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
