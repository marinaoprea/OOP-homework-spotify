package main.wrappers;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import main.Database;
import main.Wrappeable;
import main.user.Artist;
import main.user.Host;
import main.user.User;

import java.util.HashMap;

public final class Wrapper implements ObserverWrapper {
    @Getter
    private final HashMap<String, Integer> wrapArtists = new HashMap<>();
    @Getter
    private final HashMap<String, Integer> wrapGenre = new HashMap<>();
    @Getter
    private final HashMap<Wrappeable, Integer> wrapSong = new HashMap<>();
    @Getter
    private final HashMap<String, Integer> wrapAlbum = new HashMap<>();
    @Getter
    private final HashMap<Wrappeable, Integer> wrapPodcast = new HashMap<>();
    public Wrapper() {

    }

    /**
     * method updates statistics for listened song;
     * method calls for genre, artist and album statistics updates
     * @param song listened song
     * @param listens number of listens to be added
     * @param database extended input library
     * @param user listener
     */
    @Override
    public void updateSong(final SongInput song, final int listens, final Database database,
                           final User user) {
        updateGenre(song.getGenre(), listens);

        Artist artist = database.findArtist(song.getArtist());
        if (artist != null) {
            updateArtists(song.getArtist(), listens);
            artist.getWrapperArtist().updateFans(user, listens);
            artist.getWrapperArtist().updateSong(song, listens, database, user);
            artist.getRevenue().setWasListened(true);
        }

        String album = song.getAlbum();
        if (album != null) {
            this.updateAlbums(album, listens);
            if (artist != null) {
                artist.getWrapperArtist().updateAlbums(album, listens);
            }
        }
        if (!wrapSong.containsKey(song)) {
            wrapSong.put(song, listens);
            return;
        }
        Integer previousListens = wrapSong.remove(song);
        wrapSong.put(song, previousListens + listens);
    }


    /**
     * method updates genre statistics
     * @param genre genre to be updated
     * @param listens number of listens to be added
     */
    @Override
    public void updateGenre(final String genre, final int listens) {
        if (!wrapGenre.containsKey(genre)) {
            wrapGenre.put(genre, listens);
            return;
        }
        Integer previousListens = wrapGenre.remove(genre);
        wrapGenre.put(genre, previousListens + listens);
    }

    /**
     * method updates artist statistics
     * @param artist artist to be updated
     * @param listens number of listens to be added
     */
    @Override
    public void updateArtists(final String artist, final int listens) {
        if (!wrapArtists.containsKey(artist)) {
            wrapArtists.put(artist, listens);
            return;
        }
        Integer previousListens = wrapArtists.remove(artist);
        wrapArtists.put(artist, previousListens + listens);
    }


    /**
     * method that updates album statistics
     * @param album album to be updated
     * @param listens number of listens to be added
     */
    @Override
    public void updateAlbums(final String album, final int listens) {
        if (!wrapAlbum.containsKey(album)) {
            wrapAlbum.put(album, listens);
            return;
        }
        Integer previousListens = wrapAlbum.remove(album);
        wrapAlbum.put(album, previousListens + listens);
    }


    /**
     * method updates podcast statistics;
     * method calls for update of host's statistics
     * @param podcastInput podcast to be updated; used for host identification
     * @param episode episode that has been listens
     * @param listens number of listens to be added
     * @param database extended input library
     * @param user listener
     */
    @Override
    public void updatePodcasts(final PodcastInput podcastInput, final EpisodeInput episode,
                               final int listens, final Database database, final User user) {
        Host host = database.findHost(podcastInput.getOwner());
        if (host != null) {
            host.getWrapperHost().updatePodcasts(podcastInput, episode, listens, database, user);
            host.getWrapperHost().updateFans(user, listens);
        }

        if (!wrapPodcast.containsKey(episode)) {
            wrapPodcast.put(episode, listens);
            return;
        }
        Integer previousListens = wrapPodcast.remove(episode);
        wrapPodcast.put(episode, previousListens + listens);
    }
}
