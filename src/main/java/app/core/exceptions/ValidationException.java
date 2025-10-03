package app.core.exceptions;

/**
 * Purpose: To handle validation exceptions in the API
 * Author: Thomas Hartmann
 */
public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(400, message);
    }
}
