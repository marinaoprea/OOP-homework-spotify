package main.wrappers;

import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import main.Album;
import main.Database;
import main.Wrappeable;
import main.user.Artist;
import main.user.User;

import java.util.HashMap;

public class Wrapper implements ObserverWrapper {
    @Getter
    private final HashMap<Wrappeable, Integer> wrapArtists = new HashMap<>();
    @Getter
    private final HashMap<String, Integer> wrapGenre = new HashMap<>();
    @Getter
    private final HashMap<Wrappeable, Integer> wrapSong = new HashMap<>();
    @Getter
    private final HashMap<Wrappeable, Integer> wrapAlbum = new HashMap<>();
    @Getter
    private final HashMap<Wrappeable, Integer> wrapPodcast = new HashMap<>();
    public Wrapper() {

    }

    @Override
    public void updateSong(final SongInput song, final int listens, final Database database, final User user) {
        //song.setListens(song.getListens() + listens);

        updateGenre(song.getGenre(), listens);

        Artist artist = database.findArtist(song.getArtist());
        if (artist != null) {
            updateArtists(artist, listens);
            artist.getWrapperArtist().updateFans(user, listens);
            artist.getWrapperArtist().updateSong(song, listens, database, user);
            artist.getRevenue().setWasListened(true);
        }

        Album album = database.findAlbum(song.getAlbum());
        if (album != null) {
            updateAlbums(album, listens);
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
    public void updateArtists(Artist artist, int listens) {
        if (!wrapArtists.containsKey(artist)) {
            wrapArtists.put(artist, listens);
            return;
        }
        Integer previousListens = wrapArtists.remove(artist);
        wrapArtists.put(artist, previousListens + listens);
    }

    @Override
    public void updateAlbums(Album album, int listens) {
        //album.setListens(album.getListens() + listens);

        if (!wrapAlbum.containsKey(album)) {
            wrapAlbum.put(album, listens);
            return;
        }
        Integer previousListens = wrapAlbum.remove(album);
        wrapAlbum.put(album, previousListens + listens);
    }

    @Override
    public void updatePodcasts(PodcastInput podcast, int listens) {
        if (!wrapPodcast.containsKey(podcast)) {
            wrapPodcast.put(podcast, listens);
            return;
        }
        Integer previousListens = wrapPodcast.remove(podcast);
        wrapPodcast.put(podcast, previousListens + listens);
    }
}
