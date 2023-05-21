package bg.sofia.uni.fmi.mjt.spotify.database;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)


public class PlaylistsDatabaseTest {
    //love u <3
    PlaylistsDatabase playlistsDB = new PlaylistsDatabase();



    /*
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
    }*/
}
