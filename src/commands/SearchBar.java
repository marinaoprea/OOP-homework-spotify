package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import main.CommandInput;
import main.Filter;
import main.Playlist;
import main.Album;
import main.Database;
import main.user.Artist;
import main.user.Host;
import main.user.User;

import java.util.ArrayList;

public class SearchBar extends Command {
    @Getter
    private final String type;
    private final Filter filters;
    private String message;

    @Getter
    private final ArrayList<String> results = new ArrayList<String>();
    @Getter
    private final ArrayList<Integer> resultsId = new ArrayList<>();

    /**
     * constructor that constructs search bar command from input command
     * @param commandInput the input command
     */
    public SearchBar(final CommandInput commandInput) {
        super(commandInput);
        this.filters = commandInput.getFilters();
        this.type = commandInput.getType();
    }

    /**
     * verifies if song name begins with given substring
     * @param song song to validate
     * @return true for eligible; false otherwise
     */
    private boolean validateSongbyName(final SongInput song) {
        return (this.filters.getName() == null
                || song.getName().toLowerCase().indexOf(this.filters.getName().toLowerCase()) == 0);
    }

    /**
     * verifies if song is part of specific album
     * @param song song to validate
     * @return true for eligible; false otherwise
     */
    private boolean validateSongByAlbum(final SongInput song) {
        return (this.filters.getAlbum() == null || song.getAlbum().equals(this.filters.getAlbum()));
    }

    /**
     * verifies if song is tagged under specific tags
     * @param song song to validate
     * @return true for eligible; false otherwise
     */
    private boolean validateSongByTags(final SongInput song) {
        if (this.filters.getTags() == null) {
            return true;
        }
        if (song.getTags() == null) {
            return false;
        }
        for (String tag : this.filters.getTags()) {
            if (!song.getTags().contains(tag)) {
                return false;
            }
        }
        return true;
    }

    /**
     * verifies if song contains specific lyrics
     * @param song song to validate
     * @return true for eligible; false otherwise
     */
    private boolean validateSongByLyrics(final SongInput song) {
        return (this.filters.getLyrics() == null
                || song.getLyrics().toLowerCase().contains(this.filters.getLyrics().toLowerCase()));
    }

    /**
     * verifies if song is part of specific genre
     * @param song song to validate
     * @return true for eligible; false otherwise
     */
    private boolean validateSongByGenre(final SongInput song) {
        return (this.filters.getGenre() == null
                || song.getGenre().equalsIgnoreCase(this.filters.getGenre()));
    }

    /**
     * verifies if song is interpreted by specific artist
     * @param song song to validate
     * @return true for eligible; false otherwise
     */
    private boolean validateSongByArtist(final SongInput song) {
        return (this.filters.getArtist() == null
                || song.getArtist().equals(this.filters.getArtist()));
    }

    /**
     * verifies if song is released before or after specific year
     * @param song song to validate
     * @return true for eligible; false otherwise
     */
    private boolean validateSongByReleaseYear(final SongInput song) {
        if (this.filters.getReleaseYear() == null) {
            return true;
        }
        int year = Integer.parseInt(this.filters.getReleaseYear().substring(1));
        if (this.filters.getReleaseYear().contains(">")) {
            return (song.getReleaseYear() > year);
        } else {
            return (song.getReleaseYear() < year);
        }
    }

    /**
     * validates song through all given filters; if filter nonexistent,
     * validation method returns true, so it does not affect the overall result
     * performed through && logical operator
     * @param song song to be validated
     * @return true if eligible; false otherwise
     */
    boolean validateSong(final SongInput song) {
        return (validateSongbyName(song) && validateSongByAlbum(song)
                && validateSongByReleaseYear(song) && validateSongByGenre(song)
                && validateSongByLyrics(song) && validateSongByArtist(song)
                && validateSongByTags(song));
    }

    /**
     * method parses the input song list from the library and calls
     * for validation method
     * @param database extended input library
     */
    private void searchSong(final Database database) {
        for (SongInput aux : database.getSongs()) {
            if (this.validateSong(aux)) {
                this.results.add(aux.getName());
                this.resultsId.add(aux.getId());
                if (this.results.size() == Constants.NO_SEARCH_RESULTS) {
                    break;
                }
            }
        }
        this.writeMessage();
    }

    /**
     * method writes message for search command
     */
    private void writeMessage() {
        this.message = "Search returned " + this.results.size() + " results";
    }

    /**
     * validates if podcast name starts with specific substring
     * @param podcast podcast to be validated
     * @return true if eligible, false otherwise
     */
    private boolean validatePodcastByName(final PodcastInput podcast) {
        return (this.filters.getName() == null
                || podcast.getName().toLowerCase().indexOf(this.filters.getName().toLowerCase()) == 0);
    }

    /**
     * validates if podcast belongs to specific owner
     * @param podcast podcast to be validated
     * @return true if eligible, false otherwise
     */
    private boolean validatePodcastByOwner(final PodcastInput podcast) {
        return (this.filters.getOwner() == null
                || this.filters.getOwner().equals(podcast.getOwner()));
    }

    /**
     * validates podcast through all given filters; if filter nonexistent,
     * validation method returns true, so it does not affect the overall result
     * performed through && logical operator
     * @param podcast podcast to be validated
     * @return true if eligible; false otherwise
     */
    private boolean validatePodcast(final PodcastInput podcast) {
        return (validatePodcastByName(podcast) && validatePodcastByOwner(podcast));
    }

    /**
     * method parses the input song list from the library and calls
     * for validation method
     * @param database extended input library
     */
    private void searchPodcast(final Database database) {
        for (PodcastInput aux : database.getPodcasts()) {
            if (this.validatePodcast(aux)) {
                this.results.add(aux.getName());
                if (this.results.size() == Constants.NO_SEARCH_RESULTS) {
                    break;
                }
            }
        }
        this.writeMessage();
    }

    /**
     * validates if playlist belongs to specific owner; if search is conducted
     * by playlist owner, visibility field does not matter; otherwise playlist
     * should be public
     * @param playlist playlist to be validated
     * @return true if eligible, false otherwise
     */
    private boolean validatePlaylistByOwner(final Playlist playlist) {
        if (this.filters.getOwner() == null) {
            return true;
        }
        if (playlist.getUser().getUsername().equals(this.filters.getOwner())
                && playlist.getUser().getUsername().equals(this.getUsername())) {
            return true;
        }
        return (playlist.getUser().getUsername().equals(this.filters.getOwner())
                && !playlist.isVisibility());
    }

    /**
     * validates if playlist name contains specific substring; if search is conducted
     * by playlist owner, visibility field does not matter; otherwise playlist
     * should be public
     * @param playlist playlist to be validated
     * @return true if eligible, false otherwise
     */
    private boolean validatePlaylistByName(final Playlist playlist) {
        if (this.filters.getName() == null) {
            return true;
        }
        if (playlist.getName().toLowerCase().contains(this.filters.getName().toLowerCase())) {
            if (playlist.getUser().getUsername().equals(this.getUsername())) {
                // if the playlist belongs to the user
                return true;
            }
            // otherwise check visibility
            return !playlist.isVisibility();
        }
        return false;
    }

    /**
     * validates playlist through all given filters; if filter nonexistent,
     * validation method returns true, so it does not affect the overall result
     * performed through && logical operator
     * @param playlist playlist to be validated
     * @return true if eligible; false otherwise
     */
    private boolean validatePlaylist(final Playlist playlist) {
        return (validatePlaylistByName(playlist) && validatePlaylistByOwner(playlist));
    }

    /**
     * method parses the input song list from the library and calls
     * for validation method
     * @param database extension of library that contains created playlists
     */
    private void searchPlaylist(final Database database) {
        for (Playlist aux : database.getGlobalPlaylists()) {
            if (this.validatePlaylist(aux)) {
                this.results.add(aux.getName());
                if (this.results.size() == Constants.NO_SEARCH_RESULTS) {
                    break;
                }
            }
        }
        this.writeMessage();
    }

    /**
     * method parses the global list of artists and verifies if artist name
     * starts with given string;
     * if valid, method adds artist name as result; search results are limited
     * to 5
     * @param database extended input library
     */
    private void searchArtist(final Database database) {
        for (Artist artist : database.getArtists()) {
            if (artist.getUsername().startsWith(this.filters.getName())) {
                this.results.add(artist.getUsername());
                if (this.results.size() == Constants.NO_SEARCH_RESULTS) {
                    break;
                }
            }
        }
        this.writeMessage();
    }

    /**
     * validates if album name starts with specific substring
     * @param album album to be validated
     * @return true if name filter non-existent or album name is valid; false
     * otherwise
     */
    private boolean validateAlbumByName(final Album album) {
        return (this.filters.getName() == null
                || album.getName().toLowerCase().startsWith(this.filters.getName().toLowerCase()));
    }

    /**
     * validates if album has specific owner
     * @param album album to be validated
     * @return true if owner filter non-existent or album is valid; false otherwise
     */
    private boolean validateAlbumByOwner(final Album album) {
        return (this.filters.getOwner() == null
                || album.getOwner().getUsername().startsWith(this.filters.getOwner()));
    }

    /**
     * validates if album description starts with specific substring
     * @param album album to be validated
     * @return true if description filter non-existent or album is valid; false otherwise
     */
    private boolean validateAlbumByDescription(final Album album) {
        return (this.filters.getAlbum() == null
                || album.getDescription().startsWith(this.filters.getDescription()));
    }

    /**
     * validates album through all given filters; if filter nonexistent,
     * validation method returns true, so it does not affect the overall result
     * performed through && logical operator
     * @param album album to be validated
     * @return true if eligible; false otherwise
     */
    private boolean validateAlbum(final Album album) {
        return validateAlbumByName(album)
                && validateAlbumByOwner(album) && validateAlbumByDescription(album);
    }

    /**
     * method parses the album list from database and calls
     * for validation method
     * @param database extension of input library
     */
    private void searchAlbum(final Database database) {
        for (Artist artist : database.getArtists()) {
            for (Album album : artist.getAlbums()) {
                if (validateAlbum(album)) {
                    this.results.add(album.getName());
                    this.resultsId.add(album.getId());
                    if (this.results.size() == Constants.NO_SEARCH_RESULTS) {
                        break;
                    }
                }
            }
            if (this.results.size() == Constants.NO_SEARCH_RESULTS) {
                break;
            }
        }
        this.writeMessage();
    }

    /**
     * method parses the global list of hosts and verifies if host name
     * starts with given string;
     * if valid, method adds host name as result; search results are limited
     * to 5
     * @param database extended input library
     */
    private void searchHost(final Database database) {
        for (Host host: database.getHosts()) {
            if (host.getUsername().startsWith(this.filters.getName())) {
                this.results.add(host.getUsername());
                if (this.results.size() == Constants.NO_SEARCH_RESULTS) {
                    break;
                }
            }
        }
        this.writeMessage();
    }

    /**
     * method calls for specific search methods depending on search type;
     * if search is performed for a podcast, simulation up until this moment is
     * performed, in order to be able to return to same moment in podcast in case
     * of further podcast reload
     * @param database created and populated database
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "User does not exist.";
            return;
        }

        if (!user.isConnectionStatus()) {
            this.message = this.getUsername() + " is offline.";
            return;
        }

        if (user.getSelectedType().equals("podcast")) {
            user.simulate(this.getTimestamp(), database);
            String podcastName = user.getLoadedSourceName();
            User.SavedHistory savedHistory = user.findInHistory(podcastName);
            if (savedHistory == null) {
                savedHistory = new User.SavedHistory(user.getSelectedIndexInList(),
                        user.getTimeRelativeToSong(), podcastName);
                user.getHistory().add(savedHistory);
            } else {
                user.updatePodcast(savedHistory, user.getSelectedIndexInList(),
                        user.getTimeRelativeToSong());
            }
        } else {
            user.simulate(this.getTimestamp(), database);
        }
        user.setSelectedIndex(0);
        user.setIsSelected(false);
        user.setLoaded(false);
        user.setRepeat(0);
        user.setPlaying(false);
        user.setSelectedIndexInLibrary(0);

        if (this.type.equals("song")) {
            this.searchSong(database);
            database.findUserInDatabase(this.getUsername()).setLastSearch(this);
            return;
        }
        if (this.type.equals("podcast")) {
            this.searchPodcast(database);
            database.findUserInDatabase(this.getUsername()).setLastSearch(this);
            return;
        }
        if (this.type.equals("playlist")) {
            this.searchPlaylist(database);
            database.findUserInDatabase(this.getUsername()).setLastSearch(this);
            return;
        }
        if (this.type.equals("artist")) {
            this.searchArtist(database);
            database.findUserInDatabase(this.getUsername()).setLastSearch(this);
            return;
        }
        if (this.type.equals("album")) {
            this.searchAlbum(database);
            database.findUserInDatabase(this.getUsername()).setLastSearch(this);
            return;
        }
        if (this.type.equals("host")) {
            this.searchHost(database);
            database.findUserInDatabase(this.getUsername()).setLastSearch(this);
            return;
        }
    }

    /**
     * overrides conversion to ObjectNode specific for this command
     * @param objectNode created ObjectNode
     */
    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode resultsArray = mapper.createArrayNode();
        if (!this.results.isEmpty()) {
            for (String aux : this.results) {
                resultsArray.add(aux);
            }
        }
        objectNode.set("results", resultsArray);
    }
}
