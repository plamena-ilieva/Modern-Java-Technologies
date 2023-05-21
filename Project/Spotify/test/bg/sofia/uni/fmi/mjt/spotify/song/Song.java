package bg.sofia.uni.fmi.mjt.spotify.song;

import bg.sofia.uni.fmi.mjt.spotify.database.Identifiable;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Arrays;

public class Song implements Identifiable, Serializable {
    //private final String id;
    private final String title;
    private final String artist;
    private final String text;

    private final String songInfo;

    private int timesPlayed = 0;

    public Song(String title, String artist, String text) {
        //this.id = id;
        this.title = title;
        this.artist = artist;
        this.text = text;
        songInfo = "";
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getText() {
        return text;
    }

    public Path getPath() {
        return Path.of("songs", this.artist + " - " + this.title + ".wav");

    }
    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void play() {
        timesPlayed++;
    }

    public boolean containsKeyWords(String... keywords) {
        return Arrays.stream(keywords)
                .allMatch(word -> title.contains(word) || artist.contains(word) || text.contains(word));
    }

    public String getSongInfo() {
        return songInfo;
    }
    @Override
    public String getID() {
        return getTitle();
    }

    //TODO : builder for song
}
