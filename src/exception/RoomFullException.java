package exception;

/**
 * Thrown when an allocation attempt is made on a room that has reached its maximum capacity.
 */
public class RoomFullException extends Exception {
    public RoomFullException(String message) {
        super(message);
    }
}
