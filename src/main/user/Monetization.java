package main.user;

import fileio.input.SongInput;
import main.Database;

import java.util.HashMap;
import java.util.Map;

public final class Monetization {

    /**
     * method calculates monetization for artists from user's history of listened songs;
     * method updates artists' revenue;
     * method filters songs depending on artist; then divides the price between artists
     * depending on their number of listens
     * @param user user from whom history we get updates
     * @param database extended input library; used for getting list of artists
     * @param price price to be devided between artists
     */
    public static void calculateMonetization(final User user, final Database database, final double price) {
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

        for (Map.Entry<Artist, Integer> entry : songsPerArtist.entrySet()) {
            Artist artist = entry.getKey();
            Double revenue = (1.0 * entry.getValue()) * price / (1.0 * totalNumberOfSongs);
            Double previousRevenue = artist.getRevenue().getSongRevenue();
            artist.getRevenue().setSongRevenue(previousRevenue + revenue);

            for (Map.Entry<SongInput, Integer> entry1 : user.getSongHistory().getSongMap().entrySet()){
                SongInput songInput = entry1.getKey();
                if (songInput.getArtist().equals(artist.getUsername())) {
                    // total revenue / total number of listens of artist * number of listens of current song
                    Double revenuePerSong = revenue / (1.0 * entry.getValue()) * (1.0 * entry1.getValue());
                    artist.getRevenue().updateSongRevenue(songInput, revenuePerSong);
                }
            }
        }
    }
}
