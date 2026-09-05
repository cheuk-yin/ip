package buddy.exception;

/**
 * Signals that a Buddy command could not be understood or executed,
 * e.g. because a required part of the input was missing or malformed.
 */
public class BuddyException extends Exception {
    public BuddyException(String message) {
        super(message);
    }
}
