package exception;

/**
 * Thrown when a student attempts to send a duplicate roommate request to another student.
 */
public class DuplicateRequestException extends Exception {
    public DuplicateRequestException(String message) {
        super(message);
    }
}
