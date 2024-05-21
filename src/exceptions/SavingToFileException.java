package exceptions;

public class SavingToFileException extends RuntimeException {
    public SavingToFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
