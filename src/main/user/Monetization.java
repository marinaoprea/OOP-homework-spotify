package main.user;

import commands.Constants;
import fileio.input.SongInput;
import main.Database;

import java.util.HashMap;
import java.util.Map;

public class Monetization {
    public static void calculateMonetization(User user, Database database) {
        int totalNumberOfSongs = 0;
        HashMap<Artist, Integer> songsPerArtist = new HashMap<>();
        for (Map.Entry<SongInput, Integer> entry : user.getSongHistory().getSongMap().entrySet()) {
            SongInput songInput = entry.getKey();
            Artist artist = database.findArtist(songInput.getArtist());
            if (artist != null) {
                if (songsPerArtist.containsKey(artist)) {
                    Integer previousListens = songsPerArtist.remove(artist);
                    songsPerArtist.put(artist, previousListens + entry.getValue());
                } else {
                    songsPerArtist.put(artist, entry.getValue());
                }
            }
            totalNumberOfSongs += entry.getValue();
        }

        if (user.isPremium()) {
            for (Map.Entry<Artist, Integer> entry : songsPerArtist.entrySet()) {
                Artist artist = entry.getKey();
                Double revenue = entry.getValue() * Constants.PREMIUM_CREDIT / totalNumberOfSongs;
                Double previousRevenue = artist.getRevenue().getSongRevenue();
                artist.getRevenue().setSongRevenue(previousRevenue + revenue);

                for (Map.Entry<SongInput, Integer> entry1 : user.getSongHistory().getSongMap().entrySet()){
                    SongInput songInput = entry1.getKey();
                    if (songInput.getArtist().equals(artist.getUsername())) {
                        // total revenue / total number of listens of artist * number of listens of current song
                        Double revenuePerSong = revenue / entry.getValue() * entry1.getValue();
                        artist.getRevenue().updateSongRevenue(songInput, revenuePerSong);
                    }
                }
            }
        }
    }
}
