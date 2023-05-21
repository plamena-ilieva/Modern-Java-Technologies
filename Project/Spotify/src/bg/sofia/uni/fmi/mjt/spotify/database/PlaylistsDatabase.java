package bg.sofia.uni.fmi.mjt.spotify.database;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistException;
import bg.sofia.uni.fmi.mjt.spotify.song.Playlist;

public class PlaylistsDatabase extends Database<Playlist> {
    private final static String PLAYLIST_FILE_NAME = "C:\\Users\\plami\\IdeaProjects\\Spotify\\src\\bg\\sofia\\uni\\fmi\\mjt\\spotify\\database\\playlists.txt";

    public PlaylistsDatabase() {
        super(PLAYLIST_FILE_NAME);
    }

    public void addPlaylist(Playlist playlist) throws PlaylistException {
        if (super.objects.containsKey(playlist.getID())) {
            throw new PlaylistException("Playlist " + playlist.getID() + " already exists!");
        }
        super.objects.put(playlist.getID(), playlist);
    }

    public Playlist getPlaylist(String name) throws PlaylistException {
        if (!containsPlaylist(name)) {
            throw new PlaylistException("Playlist " + name + " does not exist!");
        }
        return super.objects.get(name);
    }

    public boolean containsPlaylist(String name) {
        return super.objects.containsKey(name);
    }

    public void save() {
        super.save(PLAYLIST_FILE_NAME);
    }
}
