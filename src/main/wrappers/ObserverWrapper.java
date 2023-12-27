package main.wrappers;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import main.Album;
import main.Database;
import main.user.Artist;
import main.user.User;

public interface ObserverWrapper {
    default void updateSong(final SongInput song, final int listens, final Database database, final User user) {}
    default void updateGenre(final String genre, final int listens) {}
    default void updateArtists(final Artist artist, final int listens) {}
    //default void updateAlbums(final Album album, final int listens) {}
    default void updateAlbums(final String album, final int listens) {}
    default void updatePodcasts(final PodcastInput podcastInput, final EpisodeInput episode, final int listens, final Database database, final User user) {}
    default void updateFans(final User user, final int listens) {}
}
