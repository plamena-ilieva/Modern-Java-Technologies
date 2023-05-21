package bg.sofia.uni.fmi.mjt.spotify.exceptions;

public class SongException extends Exception {
    public SongException(String message) {
        super(message);
    }

    public SongException(String message, Throwable cause) {
        super(message, cause);
    }
}
