package bg.sofia.uni.fmi.mjt.spotify.exceptions;

public class InvalidLoginDataException extends Exception {
    public InvalidLoginDataException(String message) {
        super(message);
    }

    public InvalidLoginDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
