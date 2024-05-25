package exceptions;

public class InvalidReceivedTimeException extends RuntimeException {
    public InvalidReceivedTimeException(String message) {
        super(message);
    }
}
