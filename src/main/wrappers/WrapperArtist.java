package main.wrappers;

import fileio.input.SongInput;
import lombok.Getter;
import main.Database;
import main.Wrappeable;
import main.user.User;

import java.util.HashMap;

public final class WrapperArtist implements ObserverWrapper {
    public WrapperArtist() {

    }
    private final HashMap<User, Integer> topFans = new HashMap<>();
    private final HashMap<Wrappeable, Integer> wrapSongs = new HashMap<>();
    @Getter
    private final HashMap<String, Integer> wrapAlbums = new HashMap<>();

    /**
     * method updates fans statistic
     * @param user fan to be updated in statistics
     * @param listens number of listens to be added
     */
    @Override
    public void updateFans(final User user, final int listens) {
        if (!topFans.containsKey(user)) {
            topFans.put(user, listens);
            return;
        }
        Integer previousListens = topFans.remove(user);
        topFans.put(user, previousListens + listens);
    }

    /**
     * method updates statistics for listened song;
     * @param song listened song
     * @param listens number of listens to be added
     * @param database extended input library
     * @param user listener
     */
    @Override
    public void updateSong(final SongInput song, final int listens,
                           final Database database, final User user) {
        if (!wrapSongs.containsKey(song)) {
            wrapSongs.put(song, listens);
            return;
        }
        Integer previousListens = wrapSongs.remove(song);
        wrapSongs.put(song, previousListens + listens);
    }


    /**
     * method that updates album statistics
     * @param album album to be updated
     * @param listens number of listens to be added
     */
    @Override
    public void updateAlbums(final String album, final int listens) {
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
}
