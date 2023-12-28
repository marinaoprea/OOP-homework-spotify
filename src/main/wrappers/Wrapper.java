package main.wrappers;

import commands.AddEvent;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import main.Album;
import main.Database;
import main.Wrappeable;
import main.user.Artist;
import main.user.Host;
import main.user.User;

import java.util.HashMap;

public class Wrapper implements ObserverWrapper {
    @Getter
    private final HashMap<String, Integer> wrapArtists = new HashMap<>();
    @Getter
    private final HashMap<String, Integer> wrapGenre = new HashMap<>();
    @Getter
    private final HashMap<Wrappeable, Integer> wrapSong = new HashMap<>();
    //@Getter
    //private final HashMap<Wrappeable, Integer> wrapAlbum = new HashMap<>();

    @Getter
    private final HashMap<String, Integer> wrapAlbum = new HashMap<>();
    @Getter
    private final HashMap<Wrappeable, Integer> wrapPodcast = new HashMap<>();
    public Wrapper() {

    }

    @Override
    public void updateSong(final SongInput song, final int listens, final Database database, final User user) {
        updateGenre(song.getGenre(), listens);

        Artist artist = database.findArtist(song.getArtist());
        //Artist artist = database.findArtistByNameAndAlbum(song.getArtist(), song.getAlbum());
        if (artist != null) {
            updateArtists(song.getArtist(), listens);
            artist.getWrapperArtist().updateFans(user, listens);
            artist.getWrapperArtist().updateSong(song, listens, database, user);
            artist.getRevenue().setWasListened(true);
        }

        String album = song.getAlbum();
        //Album album = database.findAlbumByNameAndArtist(song.getAlbum(), song.getArtist());
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

    @Override
    public void updateGenre(String genre, int listens) {
        if (!wrapGenre.containsKey(genre)) {
            wrapGenre.put(genre, listens);
            return;
        }
        Integer previousListens = wrapGenre.remove(genre);
        wrapGenre.put(genre, previousListens + listens);
    }

    @Override
    public void updateArtists(String artist, int listens) {
        if (!wrapArtists.containsKey(artist)) {
            wrapArtists.put(artist, listens);
            return;
        }
        Integer previousListens = wrapArtists.remove(artist);
        wrapArtists.put(artist, previousListens + listens);
    }

    @Override
    public void updateAlbums(final String album, int listens) {
        if (!wrapAlbum.containsKey(album)) {
            wrapAlbum.put(album, listens);
            return;
        }
        Integer previousListens = wrapAlbum.remove(album);
        wrapAlbum.put(album, previousListens + listens);
    }

    @Override
    public void updatePodcasts(final PodcastInput podcastInput, EpisodeInput episode, int listens, final Database database, final User user) {
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
