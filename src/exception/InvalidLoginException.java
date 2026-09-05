package exception;

/**
 * Thrown when user authentication fails due to incorrect credentials.
 */
public class InvalidLoginException extends Exception {
    public InvalidLoginException(String message) {
        super(message);
    }
}
