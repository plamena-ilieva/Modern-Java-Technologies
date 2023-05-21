package bg.sofia.uni.fmi.mjt.spotify.song;

import bg.sofia.uni.fmi.mjt.spotify.database.Identifiable;
import bg.sofia.uni.fmi.mjt.spotify.database.SongsDatabase;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.SongException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Playlist implements Identifiable, Serializable {
    private String name;
    private Set<String> songs;

    public Playlist(String name) {
        this.name = name;
        songs = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    @Override
    public String getID() {
        return getName();
    }

    public void editName(String name) {
        this.name = name;
    }

    public Set<String> getSongs() {
        return songs;
    }

    public void addSong(String songId) {
        songs.add(songId);
    }

    public String show(SongsDatabase songsDB) throws SongException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Name: ").append(name).append("\n");

        for (String songId : songs) {
            Song song = songsDB.getSong(songId);
            stringBuilder.append(song.getTitle()).append(" by ").append(song.getArtist()).append("\n");
        }
        return stringBuilder.toString();
    }
}
