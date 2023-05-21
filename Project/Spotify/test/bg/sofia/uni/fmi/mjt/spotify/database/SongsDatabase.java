package bg.sofia.uni.fmi.mjt.spotify.database;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.SongException;
import bg.sofia.uni.fmi.mjt.spotify.server.SongThread;
import bg.sofia.uni.fmi.mjt.spotify.song.Song;

import java.nio.channels.SelectionKey;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SongsDatabase extends Database<Song> {
    private final static String SONG_FILE_NAME = "C:\\Users\\plami\\IdeaProjects\\Spotify\\src\\bg\\sofia\\uni\\fmi\\mjt\\spotify\\database\\songs.txt";
    private Map<SelectionKey, SongThread> currentlyPlaying;


    public SongsDatabase() {
        super(SONG_FILE_NAME);
        currentlyPlaying = new HashMap<>();
    }

    public Set<Song> getMostPlayedSongs(int n) {
        return super.objects.values().stream()
                .sorted(Comparator.comparingInt(Song::getTimesPlayed))
                .limit(n)
                .collect(Collectors.toSet());
    }

    public void addSong(Song song) throws SongException {
        if (super.objects.containsKey(song.getID())) {
            throw new SongException("Song with id " + song.getID() + "already exists!");
        }
        super.objects.put(song.getID(), song);
    }

    public Song getSong(String id) throws SongException {
        if (!super.objects.containsKey(id)) {
            throw new SongException("Song with id " + id + "does not exist!");
        }
        return super.objects.get(id);
    }

    public Set<Song> search(String... keywords) {
        return super.objects.values().stream()
                .filter(e->e.containsKeyWords(keywords))
                .collect(Collectors.toSet());
    }

    public void playSong(SelectionKey key, String songTitle) throws SongException {
        Song song = getSong(songTitle);
        SongThread songThread = new SongThread(song, key);
        song.play();
        this.currentlyPlaying.put(key, songThread);
        songThread.start();
    }

    public void stopPlaying(SelectionKey key) throws SongException {
        if (!this.currentlyPlaying.containsKey(key)) {
            throw new SongException("No song currently playing");
        }
        this.currentlyPlaying.get(key).stopPlaying();
        try {
            this.currentlyPlaying.get(key).join();
        } catch (InterruptedException e) {
            throw new SongException("Something went wrong with the player");
            //TODO: PlayerException
        } finally {
            this.currentlyPlaying.remove(key);
        }
    }

    public void save() {
        super.save(SONG_FILE_NAME);
    }
}
