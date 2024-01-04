package main.wrappers;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import main.Database;
import main.user.User;

public interface ObserverWrapper {
    /**
     * updates song statistics
     * @param song listened song
     * @param listens number of listens to be added
     * @param database extended input library
     * @param user listener
     */
    default void updateSong(final SongInput song, final int listens,
                            final Database database, final User user) {

    }

    /**
     * updates genre statistics
     * @param genre genre to be updated
     * @param listens number of listens to be added
     */
    default void updateGenre(final String genre, final int listens) {

    }

    /**
     * updates artist statistics
     * @param artist listened artist
     * @param listens number of listens to be added
     */
    default void updateArtists(final String artist, final int listens) {

    }

    /**
     * updates album statistics
     * @param album listened album
     * @param listens number of listens to be added
     */
    default void updateAlbums(final String album, final int listens) {

    }

    /**
     * updates podcast statistics
     * @param podcastInput listened podcast
     * @param episode listened episode
     * @param listens number of listens to be added
     * @param database extended input library
     * @param user listener
     */
    default void updatePodcasts(final PodcastInput podcastInput, final EpisodeInput episode,
                                final int listens, final Database database, final User user) {

    }

    /**
     * updates fan statistics
     * @param user listener
     * @param listens number of listens to be added
     */
    default void updateFans(final User user, final int listens) {

    }
}
