package main.user;

import fileio.input.SongInput;
import lombok.Getter;
import main.Database;
import main.wrappers.ObserverWrapper;

import java.util.HashMap;

public final class History implements ObserverWrapper {
    public History() {

    }

    @Getter
    private final HashMap<SongInput, Integer> songMap = new HashMap<>();

    /**
     * method implemented for observer wrapper;
     * method updates number of listens of song in user's history
     * @param song song to be updated
     * @param listens number of listens to be added
     * @param database extended input library
     * @param user user that has listened song
     */
    @Override
    public void updateSong(final SongInput song, final int listens,
                           final Database database, final User user) {
        if (!this.songMap.containsKey(song)) {
            this.songMap.put(song, listens);
        } else {
            Integer previousListens = this.songMap.remove(song);
            this.songMap.put(song, previousListens + listens);
        }
    }

    /**
     * method performs copy of given hashmap into this hashmap;
     * method used for preserving user's history when user has bought premium in order
     * for ads to be later properly monetized after subscription cancellation
     * @param hashMap hashmap to be copied
     */
    public void copy(final HashMap<SongInput, Integer> hashMap) {
        songMap.putAll(hashMap);
    }
}
