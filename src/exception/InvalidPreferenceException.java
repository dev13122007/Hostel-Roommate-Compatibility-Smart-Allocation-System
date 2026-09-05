package exception;

/**
 * Thrown when an invalid preference category or value is supplied.
 */
public class InvalidPreferenceException extends Exception {
    public InvalidPreferenceException(String message) {
        super(message);
    }
}
