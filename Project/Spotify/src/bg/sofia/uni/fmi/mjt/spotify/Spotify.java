package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.database.PlaylistsDatabase;
import bg.sofia.uni.fmi.mjt.spotify.database.SongsDatabase;
import bg.sofia.uni.fmi.mjt.spotify.database.UsersDatabase;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.*;
import bg.sofia.uni.fmi.mjt.spotify.song.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.song.Song;
import bg.sofia.uni.fmi.mjt.spotify.user.User;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Spotify {
    UsersDatabase usersDB;
    SongsDatabase songsDB;
    PlaylistsDatabase playlistsDB;

    Map<SelectionKey, User> users;

    public Spotify() {
        usersDB = new UsersDatabase();
        songsDB = new SongsDatabase();
        playlistsDB = new PlaylistsDatabase();
        users = new HashMap<>();
    }

    public void register(SelectionKey key, String email, String password) throws UserAlreadyExitsException, InvalidPasswordException {
        User user = usersDB.register(email, password);
        users.put(key, user);
    }

    public void login(SelectionKey key, String email, String password) throws InvalidLoginDataException {
        User user = usersDB.login(email, password);
        users.put(key, user);
    }

    public void addSongToPlaylist(SelectionKey key, String playlistName, String songId)
            throws PlaylistException, SongException, UserNotLoggedIn {
        User user = getLoggedInUser(key);
        Playlist playlist = playlistsDB.getPlaylist(playlistName);
        Song song = songsDB.getSong(songId);
        user.addSongToPlaylist(playlist, song);
    }

    public void likePlaylist(SelectionKey key, String playlistName)
            throws PlaylistException, UserNotLoggedIn {
        User user = getLoggedInUser(key);
        Playlist playlist = playlistsDB.getPlaylist(playlistName);
        user.likePlaylist(playlist);
    }

    public void editPlaylistName(SelectionKey key, String playlistName, String newName)
            throws PlaylistException, UserNotLoggedIn {
        User user = getLoggedInUser(key);
        Playlist playlist = playlistsDB.getPlaylist(playlistName);
        user.editPlaylistName(playlist, newName);
    }

    public void playPlaylist(SelectionKey key, String playlistName)
            throws PlaylistException, UserNotLoggedIn, SongException {
        User user = getLoggedInUser(key);
        Playlist playlist = playlistsDB.getPlaylist(playlistName);
        //user.playPlaylist(playlist, songsDB);
    }

    public void playSong(SelectionKey key, String songId) throws SongException, UserNotLoggedIn {
        User user = getLoggedInUser(key);
        this.songsDB.playSong(key, songId);
    }

    public void stopPlaying(SelectionKey key) throws UserNotLoggedIn, SongException {
        User user = getLoggedInUser(key);
        this.songsDB.stopPlaying(key);
    }

    public String search(SelectionKey key, String[] keywords) throws UserNotLoggedIn {
        User user = getLoggedInUser(key);
        Set<Song> songs = user.search(keywords, songsDB);
        StringBuilder sb = new StringBuilder();
        songs.forEach(e -> sb.append(e.getTitle()).append(" by ").append(e.getArtist()).append("\n"));
        return sb.toString();
    }

    public String getMostPlayedSongs(SelectionKey key, int n) throws UserNotLoggedIn {
        User user = getLoggedInUser(key);
        Set<Song> songs = songsDB.getMostPlayedSongs(n);
        StringBuilder sb = new StringBuilder();
        songs.forEach(e -> sb.append(e.getTitle()).append(" by ").append(e.getArtist()).append("\n"));
        return sb.toString();
    }

    public void createPlaylist(SelectionKey key, String playlistName)
            throws PlaylistException, UserNotLoggedIn {

        User user = getLoggedInUser(key);
        if (playlistsDB.containsPlaylist(playlistName)) {
            throw new PlaylistException("Playlist " + playlistName + " already exists!");
        }
        user.createPlaylist(playlistName);
    }

    public String showPlaylist(SelectionKey key, String playlistName)
            throws PlaylistException, UserNotLoggedIn, SongException {
        User user = getLoggedInUser(key);
        Playlist playlist = playlistsDB.getPlaylist(playlistName);
        return user.showPlaylist(playlist, songsDB);
    }

    public void exit(SelectionKey key) {
        usersDB.save();
        songsDB.save();
        playlistsDB.save();
        users.remove(key);
    }

    private User getLoggedInUser(SelectionKey key) throws UserNotLoggedIn {
        if (users.containsKey(key)) {
            throw new UserNotLoggedIn("User not logged in");
        }
        return users.get(key);
    }
}
