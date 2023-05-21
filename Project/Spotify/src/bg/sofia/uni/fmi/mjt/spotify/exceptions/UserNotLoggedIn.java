package bg.sofia.uni.fmi.mjt.spotify.exceptions;

public class UserNotLoggedIn extends Exception {
    public UserNotLoggedIn(String message) {
        super(message);
    }

    public UserNotLoggedIn(String message, Throwable cause) {
        super(message, cause);
    }
}
