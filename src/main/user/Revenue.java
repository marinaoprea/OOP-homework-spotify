package main.user;

import fileio.input.SongInput;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Revenue {
    @Getter
    private Double songRevenue;
    @Getter
    private Double merchRevenue;
    @Getter
    private int ranking;
    @Getter
    private final HashMap<SongInput, Double> songProfits = new HashMap<>();
    @Getter
    private boolean wasListened = false;

    public Revenue() {
        songRevenue = 0.0;
        merchRevenue = 0.0;
    }

    /**
     * cumulated revenue
     * @return song revenue + merch revenue
     */
    public Double getTotalRevenue() {
        return songRevenue + merchRevenue;
    }


    /**
     * method gets most profitable song of artist; that is the song which has brought most song
     * revenue;
     * method sorts listened songs of artist depending on brought revenue and lexicographically
     * in case of equality
     * @return most profitable song
     */
    public SongInput getMostProfitableSong() {
        if (songProfits.isEmpty()) {
            return null;
        }

        List<Map.Entry<SongInput, Double>> sorted =
                songProfits.entrySet().stream().sorted((o1, o2) -> {
                    if (o1.getValue() > o2.getValue()) {
                        return -1;
                    }
                    if (o1.getValue() < o2.getValue()) {
                        return 1;
                    }
                    return o1.getKey().getName().compareTo(o2.getKey().getName());
                }).toList();
        return sorted.get(0).getKey();
    }

    /**
     * method updated song revenue for listened song
     * @param songInput song which provided new revenue
     * @param revenue provided revenue
     */
    public void updateSongRevenue(final SongInput songInput, final Double revenue) {
        if (this.songProfits.containsKey(songInput)) {
            Double previousRevenue = songProfits.remove(songInput);
            songProfits.put(songInput, previousRevenue + revenue);
        } else {
            songProfits.put(songInput, revenue);
        }
    }

    public void setSongRevenue(final Double songRevenue) {
        this.songRevenue = songRevenue;
    }
    public void setMerchRevenue(final Double merchRevenue) {
        this.merchRevenue = merchRevenue;
    }
    public void setRanking(final int ranking) {
        this.ranking = ranking;
    }

    public void setWasListened(final boolean wasListened) {
        this.wasListened = wasListened;
    }
}
