package exception;

/**
 * Thrown when a specific roommate request ID cannot be found.
 */
public class RequestNotFoundException extends Exception {
    public RequestNotFoundException(String message) {
        super(message);
    }
}
