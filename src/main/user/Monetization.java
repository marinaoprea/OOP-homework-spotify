package main.user;

import commands.Constants;
import fileio.input.SongInput;
import main.Database;

import java.util.HashMap;
import java.util.Map;

public class Monetization {
    public static void calculateMonetization(User user, Database database) {
        int totalNumberOfSongs = user.getSongHistory().size();
        HashMap<Artist, Integer> songsPerArtist = new HashMap<>();
        for (SongInput songInput : user.getSongHistory()) {
            Artist artist = database.findArtist(songInput.getArtist());
            if (artist != null) {
                if (songsPerArtist.containsKey(artist)) {
                    Integer previousListens = songsPerArtist.remove(artist);
                    songsPerArtist.put(artist, previousListens + 1);
                } else {
                    songsPerArtist.put(artist, 1);
                }
            }
        }

        if (user.isPremium()) {
            for (Map.Entry<Artist, Integer> entry : songsPerArtist.entrySet()) {
                Artist artist = entry.getKey();
                Double revenue = entry.getValue() * Constants.PREMIUM_CREDIT / totalNumberOfSongs;
                Double previousRevenue = artist.getRevenue().getSongRevenue();
                artist.getRevenue().setSongRevenue(previousRevenue + revenue);

                Double revenuePerSong = revenue / entry.getValue();
                for (SongInput songInput : user.getSongHistory()){
                    if (songInput.getArtist().equals(artist.getUsername())) {
                        artist.getRevenue().updateSongRevenue(songInput, revenuePerSong);
                    }
                }
            }
        }
    }
}
