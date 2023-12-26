package main.wrappers;

import fileio.input.SongInput;
import lombok.Getter;
import main.Album;
import main.Database;
import main.Wrappeable;
import main.user.User;

import java.util.HashMap;

public class WrapperArtist implements ObserverWrapper {
    public WrapperArtist() {

    }

    private final HashMap<User, Integer> topFans = new HashMap<>();

    private final HashMap<Wrappeable, Integer> wrapSongs = new HashMap<>();

    private final HashMap<String, Integer> wrapAlbums = new HashMap<>();

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
    public void updateSong(final SongInput song, final int listens, final Database database, final User user) {
        if (!wrapSongs.containsKey(song)) {
            wrapSongs.put(song, listens);
            return;
        }
        Integer previousListens = wrapSongs.remove(song);
        wrapSongs.put(song, previousListens + listens);
    }

    @Override
    public void updateAlbums(final String album,final int listens) {
        if (!wrapAlbums.containsKey(album)) {
            wrapAlbums.put(album, listens);
            return;
        }
        Integer previousListens = wrapAlbums.remove(album);
        wrapAlbums.put(album, previousListens + listens);
    }

    public HashMap<User, Integer> getTopFans() {
        return topFans;
    }

    public HashMap<Wrappeable, Integer> getWrapSongs() {
        return wrapSongs;
    }

    public HashMap<String, Integer> getWrapAlbums() {
        return wrapAlbums;
    }
}
