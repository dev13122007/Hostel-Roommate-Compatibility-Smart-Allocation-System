package exception;

/**
 * Thrown when an action (such as request sending or room assignment) is attempted on a student
 * who is already allocated to a hostel room.
 */
public class AlreadyAllocatedException extends Exception {
    public AlreadyAllocatedException(String message) {
        super(message);
    }
}
