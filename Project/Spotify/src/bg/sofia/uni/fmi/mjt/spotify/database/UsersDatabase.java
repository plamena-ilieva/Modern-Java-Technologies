package bg.sofia.uni.fmi.mjt.spotify.database;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.InvalidLoginDataException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.UserAlreadyExitsException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.UserDoesNotExistException;
import bg.sofia.uni.fmi.mjt.spotify.user.Password;
import bg.sofia.uni.fmi.mjt.spotify.user.User;

public class UsersDatabase extends Database<User> {
    public final static String USERS_FILE_NAME = "C:\\Users\\plami\\IdeaProjects\\Spotify\\src\\bg\\sofia\\uni\\fmi\\mjt\\spotify\\database\\users.txt";

    public UsersDatabase() {
        super(USERS_FILE_NAME);
    }

    public User register(String email, String password) throws UserAlreadyExitsException,
            InvalidPasswordException {
        if (super.objects.containsKey(email)) {
            throw new UserAlreadyExitsException("User with email " + email + " already exists!");
        }
        Password.validate(password);
        User user = new User(email, password);
        super.objects.put(email, user);
        return user;
    }

    public User login(String email, String password) throws InvalidLoginDataException {
        if (!super.objects.containsKey(email)) {
            throw new InvalidLoginDataException("User" + email + " does not exist!");
        }

        User user = super.objects.get(email);
        if (!user.equalsPassword(password)) {
            throw new InvalidLoginDataException("Incorrect password!");
        }

        //user.login();
        return user;
    }

    public User getUser(String username) throws UserDoesNotExistException {
        if (super.objects.containsKey(username)) {
            throw new UserDoesNotExistException("User "+ username + " does not exist!");
        }
        return super.objects.get(username);
    }

    public void save() {
        super.save(USERS_FILE_NAME);
    }
}
