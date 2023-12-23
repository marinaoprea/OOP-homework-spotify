package main.user;

import fileio.input.SongInput;
import lombok.Getter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Revenue {
    @Getter
    private Double songRevenue;
    @Getter
    private Double merchRevenue;
    @Getter
    private int ranking;
    @Getter
    private final HashMap<SongInput, Integer> songProfits = new HashMap<>();
    @Getter
    private boolean wasListened = false;

    public Revenue() {
        songRevenue = 0.0;
        merchRevenue = 0.0;
    }

    public Double getTotalRevenue() {
        return songRevenue + merchRevenue;
    }

    public SongInput getMostProfitableSong() {
        if (songProfits.isEmpty()) {
            return null;
        }

        List<Map.Entry<SongInput, Integer>> sorted =
                songProfits.entrySet().stream().sorted(new Comparator<Map.Entry<SongInput, Integer>>() {
            @Override
            public int compare(Map.Entry<SongInput, Integer> o1, Map.Entry<SongInput, Integer> o2) {
                if (o1.getValue() > o2.getValue()) {
                    return -1;
                }
                if (o1.getValue() < o2.getValue()) {
                    return 1;
                }
                return o1.getKey().getName().compareTo(o2.getKey().getName());
            }
        }).toList();
        return sorted.get(0).getKey();
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
