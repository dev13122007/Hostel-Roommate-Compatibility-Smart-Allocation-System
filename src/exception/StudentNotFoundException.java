package exception;

/**
 * Thrown when a student with a specified ID or email is not found in the system.
 */
public class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
