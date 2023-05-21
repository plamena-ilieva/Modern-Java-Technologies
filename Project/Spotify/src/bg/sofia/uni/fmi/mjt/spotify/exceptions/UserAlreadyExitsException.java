package bg.sofia.uni.fmi.mjt.spotify.exceptions;

public class UserAlreadyExitsException extends Exception {

    public UserAlreadyExitsException(String message) {
        super(message);
    }

    public UserAlreadyExitsException(String message, Throwable cause) {
        super(message, cause);
    }
}
