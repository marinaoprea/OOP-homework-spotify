package main.wrappers;

import fileio.input.SongInput;
import lombok.Getter;
import main.Album;
import main.Database;
import main.Wrappeable;
import main.user.User;

import java.util.HashMap;

public class WrapperArtist implements ObserverWrapper{
    @Getter
    private final HashMap<User, Integer> topFans = new HashMap<>();

    @Getter
    private final HashMap<Wrappeable, Integer> wrapSongs = new HashMap<>();

    @Getter
    private final HashMap<Wrappeable, Integer> wrapAlbums = new HashMap<>();

    @Override
    public void updateFans(User user, int listens) {
        if (!topFans.containsKey(user)) {
            topFans.put(user, listens);
            return;
        }
        Integer previousListens = topFans.remove(user);
        topFans.put(user, previousListens + listens);
    }

    @Override
    public void updateSong(SongInput song, int listens, Database database, User user) {
        if (!wrapSongs.containsKey(song)) {
            wrapSongs.put(song, listens);
            return;
        }
        Integer previousListens = wrapSongs.remove(song);
        wrapSongs.put(song, previousListens + listens);
    }

    @Override
    public void updateAlbums(Album album, int listens) {
        if (!wrapAlbums.containsKey(album)) {
            wrapAlbums.put(album, listens);
            return;
        }
        Integer previousListens = wrapAlbums.remove(album);
        wrapAlbums.put(album, previousListens + listens);
    }
}
