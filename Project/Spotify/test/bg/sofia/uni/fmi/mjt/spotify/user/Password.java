package bg.sofia.uni.fmi.mjt.spotify.user;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.InvalidPasswordException;

public class Password {
    public final static int MIN_LENGTH = 12;

    public static void validate(String password) throws InvalidPasswordException {
        if (password.length() < MIN_LENGTH) {
            throw new InvalidPasswordException("Password should be at least 12 characters!");
        }

        if (password.equals(password.toLowerCase())) {
            throw new InvalidPasswordException("Password should contain at least 1 upper-case letter!");
        }

        for(char ch: password.toCharArray()) {
            if (Character.isDigit(ch)) {
                return;
            }
        }
        throw new InvalidPasswordException("Password should contain at least 1 number!");
    }
}
