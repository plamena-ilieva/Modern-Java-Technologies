package bg.sofia.uni.fmi.mjt.spotify.command;

import bg.sofia.uni.fmi.mjt.spotify.Spotify;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.*;

import java.nio.channels.SelectionKey;

public class CommandExecutor {
    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String DISCONNECT = "disconnect";
    private static final String SEARCH = "search";
    private static final String TOP = "top";
    private static final String CREATE_PLAYLIST = "create-playlist";
    private static final String ADD_SONG = "add-song-to";
    private static final String SHOW_PLAYLIST = "show-playlist";
    private static final String PLAY = "play";
    private static final String STOP = "stop";
    private static final String ERROR = "Error: ";
    private static final String UNKNOWN_COMMAND =
            "Unknown command";
    private Spotify spotify;

    public CommandExecutor(Spotify spotify) {
        this.spotify = spotify;
    }

    public String execute(SelectionKey key, Command cmd) {
        try {
            return switch (cmd.command()) {
                case REGISTER -> register(key, cmd.arguments()[0], cmd.arguments()[1]);
                case LOGIN -> login(key, cmd.arguments()[0], cmd.arguments()[1]);
                case DISCONNECT -> disconnect(key);
                case SEARCH -> search(key, cmd.arguments());
                case TOP -> top(key, cmd.arguments()[0]);
                case CREATE_PLAYLIST -> createPlaylist(key, cmd.arguments()[0].replaceAll("_", " "));
                case ADD_SONG -> addSongToPlaylist(key, cmd.arguments()[0].replaceAll("_", " "),
                        cmd.arguments()[0].replaceAll("_", " "));
                case SHOW_PLAYLIST -> showPlaylist(key, cmd.arguments()[0].replaceAll("_", " "));
                case PLAY -> play(key, cmd.arguments()[0].replaceAll("_", " "));
                case STOP -> stop(key);
                default -> UNKNOWN_COMMAND;
            };
        } catch (Exception e) {
            return ERROR + e.getMessage();
        }
    }

    private String register(SelectionKey key, String email, String password) throws UserAlreadyExitsException,
            InvalidPasswordException {
        this.spotify.register(key, email, password);
        return String.format("User with email %s registered", email);
    }

    private String login(SelectionKey key, String email, String password) throws InvalidLoginDataException {
        this.spotify.login(key, email, password);
        return String.format("User with email %s successfully logged in", email);
    }

    private String disconnect(SelectionKey key) {
        this.spotify.exit(key);
        return "Disconnected";
    }

    private String search(SelectionKey key, String[] arguments) throws UserNotLoggedIn {
        return this.spotify.search(key, arguments);
    }

    private String top(SelectionKey key, String numberOfTopSongs) throws UserNotLoggedIn {
        return this.spotify.getMostPlayedSongs(key, Integer.parseInt(numberOfTopSongs));
    }

    private String createPlaylist(SelectionKey key, String playlistName) throws PlaylistException, UserNotLoggedIn {
        this.spotify.createPlaylist(key, playlistName);
        return String.format("Playlist %s created successfully", playlistName);
    }

    private String addSongToPlaylist(SelectionKey key, String playlistName, String songName)
            throws PlaylistException, UserNotLoggedIn, SongException {
        this.spotify.addSongToPlaylist(key, playlistName, songName);
        return String.format("Song %s successfully added to playlist %s", songName, playlistName);

    }

    private String showPlaylist(SelectionKey key, String playListName) throws PlaylistException,
            UserNotLoggedIn, SongException {
        return this.spotify.showPlaylist(key, playListName);
    }

    private String stop(SelectionKey key) throws UserNotLoggedIn, SongException {
        this.spotify.stopPlaying(key);
        return "No longer playing music";
    }

    private String play(SelectionKey key, String songName) throws UserNotLoggedIn, SongException {
        this.spotify.playSong(key, songName);
        return "Now playing " + songName;
    }

    //TODO: validations
    //TODO: exception handling
}