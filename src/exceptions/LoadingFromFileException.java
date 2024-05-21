package exceptions;

public class LoadingFromFileException extends RuntimeException {
    public LoadingFromFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
