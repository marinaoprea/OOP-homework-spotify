package main;

import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;
import main.user.Artist;
import main.user.Host;
import main.user.User;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Objects;

public final class Database {
    @Getter
    private int songId;

    @Getter
    private final ArrayList<Integer> noLikesPerSong = new ArrayList<Integer>();
    @Getter
    private final ArrayList<User> users = new ArrayList<User>();
    @Getter
    private final ArrayList<SongInput> songs = new ArrayList<SongInput>();
    @Getter
    private final ArrayList<PodcastInput> podcasts = new ArrayList<PodcastInput>();
    @Getter
    private final ArrayList<Playlist> globalPlaylists = new ArrayList<Playlist>();
    @Getter
    private final ArrayList<Artist> artists = new ArrayList<Artist>();
    @Getter
    private final ArrayList<User> normalUsers = new ArrayList<User>();
    private final ArrayList<Album> albums = new ArrayList<Album>();
    @Getter
    private final ArrayList<Host> hosts = new ArrayList<Host>();

    /**
     * constructor that constructs database from input library
     * @param library input library
     */
    public Database(final LibraryInput library) {
        for (UserInput aux :  library.getUsers()) {
            User user = new User(aux);
            this.users.add(user);
            this.normalUsers.add(user);
        }
        this.songs.addAll(library.getSongs());
        this.podcasts.addAll(library.getPodcasts());
        for (int i = 0; i < songs.size(); i++) {
            noLikesPerSong.add(0);
        }

        for (SongInput songInput : library.getSongs()) {
            this.songId++;
            songInput.setId(this.songId);
            if (!this.findArtistByName(songInput.getArtist())) {
                Artist artist = new Artist(songInput.getArtist());
                this.artists.add(artist);
            }
        }
    }

    /**
     * method increments number of likes of song with given index in song list
     * @param index given index of song
     */
    public void incrementLikes(final int index) {
        Integer previous = this.noLikesPerSong.get(index);
        this.noLikesPerSong.set(index, previous + 1);
    }

    /**
     * method decrements number of likes of song with given index in song list
     * @param index given index of song
     */
    public void decrementLikes(final int index) {
        Integer previous = this.noLikesPerSong.get(index);
        this.noLikesPerSong.set(index, previous - 1);
    }

    /**
     * method finds playlist in database based on playlist name
     * @param playlistName the name of the playlist
     * @return found playlist; null if nonexistent
     */
    public Playlist findPlaylistInDatabase(final String playlistName) {
        if (globalPlaylists.isEmpty()) {
            return null;
        }
        for (Playlist aux : globalPlaylists) {
            if (aux.getName().equals(playlistName)) {
                return aux;
            }
        }
        return null;
    }

    /**
     * method finds user in database based on playlist name
     * @param name the name of the user
     * @return found user; null if nonexistent
     */
    public User findUserInDatabase(final String name) {
        for (User aux : this.users) {
            if (aux.getUsername().equals(name)) {
                return aux;
            }
        }
        return null;
    }

    /**
     * method finds song in database based on song name
     * @param songName the name of the song
     * @return found song; null if nonexistent
     */
    public SongInput findSong(final String songName, final Integer id) {
        for (SongInput song : this.songs) {
            if (song.getName().equals(songName) && Objects.equals(song.getId(), id)) {
                return song;
            }
        }
        return null;
    }

    /**
     * method finds podcast in database based on podcast name
     * @param podcastName the name of the podcast
     * @return found podcast; null if nonexistent
     */
    public PodcastInput findPodcast(final String podcastName) {
        for (PodcastInput podcast : this.podcasts) {
            if (podcast.getName().equals(podcastName)) {
                return podcast;
            }
        }
        return null;
    }

    /**
     * method searches artist by name in global list of artists
     * @param artistName name of searched artist
     * @return found artists, if exists; null otherwise
     */
    public Artist findArtist(final String artistName) {
        for (Artist artist: artists) {
            if (artist.getUsername().equals(artistName)) {
                return artist;
            }
        }
        return null;
    }

    /**
     * method searches album by name in global list of albums
     * @param albumName name of searched album
     * @return found album if exists; null otherwise
     */
    public Album findAlbum(final String albumName) {
        for (Album album: albums) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }
        return null;
    }

    /**
     * method checks if database contains host with give name
     * @param hostName name of host to be validated
     * @return true if host was found; false otherwise
     */
    public boolean findHostByName(final String hostName) {
        for (Host host : this.getHosts()) {
            if (host.getUsername().equals(hostName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * method searches host by name in global list of hosts
     * @param hostName the name of the searched host
     * @return found host if existent; null otherwise
     */
    public Host findHost(final String hostName) {
        for (Host host: hosts) {
            if (host.getUsername().equals(hostName)) {
                return host;
            }
        }
        return null;
    }

    /**
     * method checks if database contains artist with give name
     * @param artistName name of artist to be validated
     * @return true if artist was found; false otherwise
     */
    public boolean findArtistByName(final String artistName) {
        for (Artist artist: artists) {
            if (artist.getUsername().equals(artistName)) {
                return true;
            }
        }
        return false;
    }

    public void simulateAllUsers(final int timestamp) {
        for (User user : users) {
            user.simulate(timestamp, this);
        }
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }
}
