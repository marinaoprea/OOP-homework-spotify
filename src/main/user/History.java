package main.user;

import fileio.input.SongInput;
import lombok.Getter;
import main.Database;
import main.wrappers.ObserverWrapper;

import java.util.HashMap;
import java.util.Map;

public class History implements ObserverWrapper {
    public History() {}

    @Getter
    private final HashMap<SongInput, Integer> songMap = new HashMap<>();

    @Override
    public void updateSong(SongInput song, int listens, Database database, User user) {
        if (!this.songMap.containsKey(song)) {
            this.songMap.put(song, listens);
        } else {
            Integer previousListens = this.songMap.remove(song);
            this.songMap.put(song, previousListens + listens);
        }
    }

    public void copy(HashMap<SongInput, Integer> hashMap) {
        for (Map.Entry<SongInput, Integer> entry : hashMap.entrySet()) {
            songMap.put(entry.getKey(), entry.getValue());
        }
    }
}
