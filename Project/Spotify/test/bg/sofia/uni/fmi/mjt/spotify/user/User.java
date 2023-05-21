package bg.sofia.uni.fmi.mjt.spotify.user;

import bg.sofia.uni.fmi.mjt.spotify.database.Identifiable;
import bg.sofia.uni.fmi.mjt.spotify.database.SongsDatabase;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.SongException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.UserNotLoggedIn;
import bg.sofia.uni.fmi.mjt.spotify.song.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.song.Song;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class User implements Identifiable, Serializable {
    private final String email;
    private final String password;
    private Set<String> myPlaylists;
    private Set<String> likedPlaylists;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        myPlaylists = new HashSet<>();
        likedPlaylists = new HashSet<>();
    }

    public boolean equalsPassword(String password) {
        return this.password.equals(password);
    }

    public void createPlaylist(String name) {
        myPlaylists.add(name);
    }

    public void addSongToPlaylist(Playlist playlist, Song song)
            throws PlaylistException {
        if (!myPlaylists.contains(playlist.getName())) {
            throw new PlaylistException("Playlist " + playlist.getName() + " does not exist in your playlists!");
        }

        playlist.addSong(song.getID());
    }

    public void likePlaylist(Playlist playlist)
            throws PlaylistException {
        if (likedPlaylists.contains(playlist.getName())) {
            throw new PlaylistException("Playlist " + playlist.getName() + "already liked!");
        }
        likedPlaylists.add(playlist.getName());
    }

    public void editPlaylistName(Playlist playlist, String newName)
            throws PlaylistException {
        if (!myPlaylists.contains(playlist.getName())) {
            throw new PlaylistException("Playlist " + playlist.getName() + " does not exist in your playlists!");
        }

        playlist.editName(newName);

    }

    /*public void playPlaylist(Playlist playlist, SongsDatabase songsDB)
            throws SongException {

        for (var songId:playlist.getSongs()) {
            playSong(songId, songsDB);
        }
    }

    public void playSong(String songId, SongsDatabase songsDB) throws SongException {
        songsDB.getSong(songId).play();
    }*/

    public Set<Song> search(String[] keywords, SongsDatabase songsDB) {
        return songsDB.search(keywords);
    }

    public String showPlaylist(Playlist playlist, SongsDatabase songsDB) throws UserNotLoggedIn, SongException {
        return playlist.show(songsDB);
    }

    @Override
    public String getID() {
        return email;
    }

    /*private void validateUser() throws UserNotLoggedIn {
        if (!isLoggedIn) {
            throw new UserNotLoggedIn("User with email " + email + " not logged in!");
        }
    }*/
}
