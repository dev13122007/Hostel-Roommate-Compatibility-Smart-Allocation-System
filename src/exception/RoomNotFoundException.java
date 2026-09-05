package exception;

/**
 * Thrown when a hostel room number is not found in the records.
 */
public class RoomNotFoundException extends Exception {
    public RoomNotFoundException(String message) {
        super(message);
    }
}
