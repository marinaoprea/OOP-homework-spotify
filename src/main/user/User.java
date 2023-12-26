package main.user;

import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import commands.SearchBar;
import lombok.Getter;
import main.Album;
import main.Database;
import main.Playlist;
import main.wrappers.Wrapper;
import pages.*;
import main.Notification;
import pages.pageNavigation.NavigationInvoker;

public class User implements ObserveContentCreators {
    @Getter
    private Page currentPage;
    @Getter
    private final HomePage homePage = new HomePage(this);
    @Getter
    private final LikedContent likedContent = new LikedContent(this);
    @Getter
    private final Playlist favourites = new Playlist(this);
    @Getter
    private final ArrayList<Playlist> myPlaylists = new ArrayList<Playlist>();
    @Getter
    private String selectedType = "";
    @Getter
    private String username;
    @Getter
    private int age;
    @Getter
    private String city;
    @Getter
    private SearchBar lastSearch;
    @Getter
    private int selectedIndex;
    @Getter
    private int selectedIndexInList;
    @Getter
    private boolean playing;
    @Getter
    private int timeLoaded;
    @Getter
    private int timeRelativeToSong;
    private boolean isSelected = false;
    private boolean isLoaded = false;
    @Getter
    private int selectedIndexInLibrary = 0;
    @Getter
    private int playlistids = 0;
    @Getter
    private ArrayList<Playlist> followed = new ArrayList<Playlist>();
    @Getter
    private int repeat;
    private boolean shuffle;
    @Getter
    private final ArrayList<SavedHistory> history = new ArrayList<SavedHistory>();
    @Getter
    private boolean connectionStatus = true; // true -> online, false -> offline
    @Getter
    private ArrayList<Integer> shuffledIndexes;
    @Getter
    private int shuffleSeed;
    @Getter
    private final Wrapper wrapper = new Wrapper();
    @Getter
    private final ArrayList<ContentCreator> subscriptions = new ArrayList<>();
    @Getter
    private final ArrayList<Notification> notifications = new ArrayList<>();
    @Getter
    private final ArrayList<Artist.Merch> boughtMerch = new ArrayList<>();
    @Getter
    private final HashSet<SongInput> songHistory = new HashSet<>();
    @Getter
    private boolean premium;
    @Getter
    private final NavigationInvoker navigation = new NavigationInvoker();

    /**
     * constructor
     */
    public User() {

    }
    /**
     * constructor that sets username, age and city from the one of the input user;
     * user's current page is set on user's homePage
     * @param userInput the input user we take information from
     */
    public User(final UserInput userInput) {
        this.username = userInput.getUsername();
        this.age = userInput.getAge();
        this.city = userInput.getCity();
        this.currentPage = homePage;
    }

    /**
     * constructor;
     * user's current page is set on user's homePage
     * @param username username of new user
     * @param age age of new user
     * @param city city of new user
     */
    public User(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.currentPage = homePage;
    }

    @Override
    public void update(Notification newNotification) {
        this.notifications.add(newNotification);
    }

    /**
     * method checks if user could be deleted; that is any other user is not currently
     * listening to any of this user's playlists
     * @param timestamp current timestamp used for getting updated information from the
     *                  statuses of other users
     * @param database extended input library
     * @return true if user could be deleted; false otherwise
     */
    public boolean check(final int timestamp, final Database database) {
        for (Playlist playlist: this.myPlaylists) {
            if (!playlist.checkDelete(timestamp, database)) {
                return false;
            }
        }
        return true;
    }

    /**
     * method clears user's contribution;
     * method updates number of likes of songs in favourites playlist;
     * method updates number of followers of followed playlists by this user;
     * method removes all user's playlists from other user's followed playlists list;
     * method removes all user's playlists from global list of playlists
     * @param database extended input library
     */
    public void clear(final Database database) {
        for (SongInput songInput: favourites.getSongs()) {
            songInput.setNoLikes(songInput.getNoLikes() - 1);
            int index = database.getSongs().indexOf(songInput);
            database.decrementLikes(index);
        }
        for (Playlist playlist: this.followed) {
            playlist.setFollowers(playlist.getFollowers() - 1);
        }
        for (Playlist playlist: this.myPlaylists) {
            for (User user: database.getUsers()) {
                user.getFollowed().remove(playlist);
            }
            database.getGlobalPlaylists().remove(playlist);
        }
    }

    /**
     * method checks if user follows specific playlist
     * @param playlist searched playlist
     * @return true if playlist is followed by the user; false otherwise
     */
    public boolean findFollowedPlaylist(final Playlist playlist) {
        if (followed.isEmpty()) {
            return false;
        }
        return followed.contains(playlist);
    }

    /**
     * methods adds playlist in user's followed collection
     * @param playlist playlist to be added
     */
    public void followPlaylist(final Playlist playlist) {
        followed.add(playlist);
    }

    /**
     * method removes playlist from user's followed collection
     * @param playlist playlist to be removed
     */
    public void unfollowPlaylist(final Playlist playlist) {
        followed.remove(playlist);
    }

    /**
     * method gets loaded song from user
     * @param database extended library input
     * @return loaded song; null if nonexistent
     */
    public SongInput getSongFromUser(final Database database) {
        if (this.lastSearch == null) {
            return null;
        }
        if (this.selectedIndexInLibrary >= 1
                && this.selectedIndexInLibrary <= database.getSongs().size()) {
            return database.getSongs().get(this.selectedIndexInLibrary - 1);
        }
        if (this.selectedIndex >= 1
                && this.selectedIndex <= this.lastSearch.getResults().size()) {
            String songName = this.lastSearch.getResults().get(this.selectedIndex - 1);
            return database.findSong(songName, this.getLoadedSourceId());
        }
        return null;
    }

    /**
     * method gets loaded song contained in loaded album
     * @param database extended input library
     * @return listened song if existent; null otherwise
     */
    public SongInput getSongFromUserInAlbum(final Database database) {
        String albumName = this.getLoadedSourceName();
        if (albumName.isEmpty()) {
            return null;
        }
        Album album = database.findAlbum(albumName);
        if (this.getSelectedIndexInList() >= 1
                && this.selectedIndexInList <= album.getSongs().size()) {
            return album.getSongs().get(this.selectedIndexInList - 1);
        }
        return null;
    }

    public SongInput getSongFromUserInPlaylist(final Database database) {
        String playlistName = this.getLoadedSourceName();
        if (playlistName.isEmpty()) {
            return null;
        }
        Playlist playlist = database.findPlaylistInDatabase(playlistName);
        if (this.getSelectedIndexInList() >= 1
                && this.selectedIndexInList <= playlist.getSongs().size()) {
            return playlist.getSongs().get(this.selectedIndexInList - 1);
        }
        return null;
    }

    /**
     * method searches playlist in user's created playlists;
     * @param playlistName name of the searched playlist
     * @return found playlist; null if nonexistent
     */
    public Playlist findPlaylist(final String playlistName) {
        for (Playlist playlist : myPlaylists) {
            if (playlist.getName().equals(playlistName)) {
                return playlist;
            }
        }
        return null;
    }
    /**
     * method searches playlist in user's created playlists;
     * @param playlistID the ID of the searched playlist
     * @return found playlist; null if nonexistent;
     */
    public Playlist findPlaylist(final int playlistID) {
        for (Playlist playlist : myPlaylists) {
            if (playlist.getPlaylistid() == playlistID) {
                return playlist;
            }
        }
        return null;
    }

    public static class SavedHistory {
        private int selectedIndexInList;
        private int timeRelativeToSong;
        private String podcastName;
        public SavedHistory() {

        }
        public SavedHistory(final int selectedIndexInList,
                            final int timeRelativeToSong, final String podcastName) {
            this.selectedIndexInList = selectedIndexInList;
            this.timeRelativeToSong = timeRelativeToSong;
            this.podcastName = podcastName;
        }
    }

    /**
     * method checks if podcast with specific given name is saved in user history;
     * @param podcastName  the name of the podcast
     * @return savedHistory structure if podcast found; null otherwise;
     */
    public SavedHistory findInHistory(final String podcastName) {
        if (history.isEmpty()) {
            return null;
        }
        for (SavedHistory savedHistory : history) {
            if (savedHistory.podcastName.equals(podcastName)) {
                return savedHistory;
            }
        }
        return null;
    }

    /**
     * method updates podcast status
     * @param savedHistory podcast structure to be updated
     * @param newSelectedIndexInList new episode index
     * @param newTimeRelativeToSong new cursor relative to episode
     */
    public void updatePodcast(final SavedHistory savedHistory, final int newSelectedIndexInList,
                              final int newTimeRelativeToSong) {
        savedHistory.selectedIndexInList = newSelectedIndexInList;
        savedHistory.timeRelativeToSong = newTimeRelativeToSong;
    }

    /**
     * method restores reloaded podcast according to the history saved,
     * if found in history; if not found, podcast is set to play from the top
     * @param podcastName the name of the podcast
     */
    public void restorePodcast(final String podcastName) {
        if (!history.isEmpty()) {
            for (SavedHistory aux : history) {
                if (aux.podcastName.equals(podcastName)) {
                    this.selectedIndexInList = aux.selectedIndexInList;
                    this.timeRelativeToSong = aux.timeRelativeToSong;
                    return;
                }
            }
        }
        this.selectedIndexInList = 1;
        this.timeRelativeToSong = 0;
    }

    /**
     * method simulates selected song up until given moment;
     * method checks if user was set on playing and then simulates song taking
     * into consideration repeat status;
     * if song is not finished we update the cursor and set the time of the update to current
     * timestamp;
     * if song has finished, we check repeat status;
     * if "No Repeat", user is set on pause;
     * if "Repeat Once", song is simulated one more time; if remaining time is still negative,
     * it means user had gotten on pause and we set it accordingly;
     * if "Repeat Infinite", we simulate the song playing infinitely and update cursor
     * corresponding to this timestamp and set the time of the update to current timestamp
     * @param timestamp timestamp up to which we simulate
     * @param database extended input library
     */
    private void simulateSong(final int timestamp, final Database database) {
        SongInput song = this.getSongFromUser(database);

        if (song == null) {
            return;
        }

        int remainedTime = song.getDuration() - this.timeRelativeToSong;

        if (this.playing) {
            remainedTime = remainedTime - (timestamp - timeLoaded);
            if (remainedTime <= 0) {
                if (this.repeat == 0) { // no repeat
                    this.playing = false;
                    this.timeLoaded = 0;
                    this.isLoaded = false;
                    this.selectedIndex = 0;
                    this.timeRelativeToSong = 0;
                } else if (this.repeat == 1) { //repeat once
                    remainedTime += song.getDuration();
                    wrapper.updateSong(song, 1, database, this);
                    if (remainedTime > 0) {
                        timeRelativeToSong = song.getDuration() - remainedTime;
                        timeLoaded = timestamp;
                        repeat = 0;
                    } else {
                        this.playing = false;
                        this.timeLoaded = 0;
                        this.selectedIndex = 0;
                        this.timeRelativeToSong = 0;
                        repeat = 0;
                    }
                } else { //repeat infinite
                    int aux = timeRelativeToSong;
                    timeRelativeToSong =
                            (timeRelativeToSong + timestamp - timeLoaded) % song.getDuration();
                    wrapper.updateSong(song, 1 +
                            (aux + timestamp - timeLoaded) / song.getDuration(), database, this);
                    timeLoaded = timestamp;
                }
            } else {
                timeRelativeToSong = song.getDuration() - remainedTime;
                timeLoaded = timestamp;
            }
        }
    }

    /**
     * method simulates podcast up until given moment;
     * method checks if user was set on playing and then simulates podcast taking
     * into consideration repeat status;
     * if "No Repeat", we simulate episodes one after another and if remaining time is still
     * negative it means podcast had ended and we set user on pause;
     * if "Repeat Once", we simulate the current episode being played once more and if not
     * sufficient we set user on pause;
     * if "Repeat Infinitely" we simulate current episode being played continuously and update
     * the cursor and update time accordingly
     * @param timestamp up to which we simulate
     * @param database extended input library
     */
    public void simulatePodcast(final int timestamp, final Database database) {
        if (selectedIndex > 0 && selectedIndex <= this.lastSearch.getResults().size()) {
            String podcastName = this.lastSearch.getResults().get(selectedIndex - 1);
            PodcastInput podcast = database.findPodcast(podcastName);
            if (playing) {
                int remainedTimeInEpisode =
                        podcast.getEpisodes().get(selectedIndexInList - 1).getDuration()
                                - timeRelativeToSong;
                remainedTimeInEpisode = remainedTimeInEpisode - (timestamp - timeLoaded);
                if (remainedTimeInEpisode > 0) {
                    timeRelativeToSong =
                            podcast.getEpisodes().get(selectedIndexInList - 1).getDuration()
                                    - remainedTimeInEpisode;
                    timeLoaded = timestamp;
                    return;
                }
                if (repeat == 0) { // no repeat
                    selectedIndexInList++;
                    while (selectedIndexInList <= podcast.getEpisodes().size()
                            && remainedTimeInEpisode < 0) {
                        remainedTimeInEpisode +=
                                podcast.getEpisodes().get(selectedIndexInList - 1).getDuration();
                        selectedIndexInList++;
                    }
                    if (remainedTimeInEpisode < 0) { // podcast ended
                        selectedIndex = 0;
                        selectedIndexInList = 0;
                        playing = false;
                        timeLoaded = 0;
                        timeRelativeToSong = 0;
                        return;
                    }
                    selectedIndexInList--; // current episode
                    timeRelativeToSong =
                            podcast.getEpisodes().get(selectedIndexInList - 1).getDuration()
                                    - remainedTimeInEpisode;
                    timeLoaded = timestamp;
                    return;
                }
                if (repeat == 1) { //repeat once
                    remainedTimeInEpisode +=
                            podcast.getEpisodes().get(selectedIndexInList - 1).getDuration();
                    if (remainedTimeInEpisode > 0) {
                        timeRelativeToSong =
                                podcast.getEpisodes().get(selectedIndexInList - 1).getDuration()
                                        - remainedTimeInEpisode;
                        timeLoaded = timestamp;
                        repeat = 0; // no repeat after this;
                        return;
                    }
                    // episode ended, podcast in no more played
                    selectedIndex = 0;
                    selectedIndexInList = 0;
                    playing = false;
                    timeLoaded = 0;
                    timeRelativeToSong = 0;
                    return;
                }
                // podcast episode repeated infinitely
                timeRelativeToSong = (timestamp - timeLoaded)
                        % podcast.getEpisodes().get(selectedIndexInList - 1).getDuration();
                timeLoaded = timestamp;
            }
            // podcast is on pause; nothing changes
        }
        // no podcast to simulate
    }

    /**
     * method simulates playlist up until given moment;
     * method checks if user was set on playing and then simulates playlist taking
     * into consideration repeat status and shuffle status;
     * if "No Repeat", we simulate songs one after another and if remaining time is still
     * negative it means playlist had ended and we set user on pause;
     * if "Repeat Current Song" we simulate current song being played continuously and update
     * the cursor and update time accordingly;
     * if "Repeat All" we simulate the playlist being played from the top and update current
     * song accordingly;
     * if shuffle is deactivated we consider the order the songs were introduced into the
     * playlist; otherwise we take into consideration the array of shuffled indexes
     * @param timestamp up to which we simulate
     * @param database extended input library
     */
    public void simulatePlaylist(final int timestamp, final Database database) {
        if (selectedIndex > 0 && selectedIndex <= this.lastSearch.getResults().size()) {
            String playlistName = this.lastSearch.getResults().get(selectedIndex - 1);
            Playlist playlist = database.findPlaylistInDatabase(playlistName);

            if (playlist == null) {
                return;
            }

            if (playing) {
                if (selectedIndexInList < 1 || selectedIndexInList > playlist.getSongs().size()) {
                    return;
                }

                int remainedTimeInEpisode =
                        playlist.getSongs().get(selectedIndexInList - 1).getDuration()
                                - timeRelativeToSong;
                remainedTimeInEpisode = remainedTimeInEpisode - (timestamp - timeLoaded);
                if (remainedTimeInEpisode > 0) {
                    timeRelativeToSong =
                            playlist.getSongs().get(selectedIndexInList - 1).getDuration()
                                    - remainedTimeInEpisode;
                    timeLoaded = timestamp;
                    return;
                }
                if (repeat == 0) { // no repeat
                    if (!this.getShuffle()) {
                        selectedIndexInList++;
                        while (selectedIndexInList <= playlist.getSongs().size()
                                && remainedTimeInEpisode <= 0) {
                            remainedTimeInEpisode +=
                                    playlist.getSongs().get(selectedIndexInList - 1).getDuration();
                            wrapper.updateSong(playlist.getSongs().get(selectedIndexInList - 1), 1, database, this);
                            this.songHistory.add(playlist.getSongs().get(selectedIndexInList - 1));
                            selectedIndexInList++;
                        }
                        if (remainedTimeInEpisode <= 0) { // playlist ended
                            selectedIndex = 0;
                            selectedIndexInList = 0;
                            playing = false;
                            timeLoaded = 0;
                            timeRelativeToSong = 0;
                            isLoaded = false;
                            isSelected = false;
                            return;
                        }
                        selectedIndexInList--; // current episode
                        timeRelativeToSong =
                                playlist.getSongs().get(selectedIndexInList - 1).getDuration()
                                        - remainedTimeInEpisode;
                        timeLoaded = timestamp;
                        return;
                    }
                    // shuffle but no repeat
                    int lastIndex = selectedIndexInList;
                    int shuffleIndex =
                            this.getShuffledIndexes().indexOf(selectedIndexInList - 1) + 1;
                    shuffleIndex++;
                    while (shuffleIndex <= playlist.getSongs().size()
                            && remainedTimeInEpisode <= 0) {
                        selectedIndexInList =
                                this.getShuffledIndexes().get(shuffleIndex - 1) + 1;
                        remainedTimeInEpisode +=
                                playlist.getSongs().get(selectedIndexInList - 1).getDuration();
                        wrapper.updateSong(playlist.getSongs().get(selectedIndexInList - 1), 1, database, this);
                        this.songHistory.add(playlist.getSongs().get(selectedIndexInList - 1));
                        lastIndex = selectedIndexInList;
                        shuffleIndex++;
                    }

                    if (remainedTimeInEpisode < 0) { // playlist ended
                        selectedIndex = 0;
                        selectedIndexInList = 0;
                        playing = false;
                        timeLoaded = 0;
                        timeRelativeToSong = 0;
                        isLoaded = false;
                        isSelected = false;
                        shuffle = false;
                        return;
                    }

                    selectedIndexInList = lastIndex;
                    timeRelativeToSong =
                            playlist.getSongs().get(selectedIndexInList - 1).getDuration()
                                    - remainedTimeInEpisode;
                    timeLoaded = timestamp;
                    return;
                }
                if (repeat == 1) { //repeat all
                    if (!this.getShuffle()) { // no shuffle
                        selectedIndexInList++;
                        while (remainedTimeInEpisode <= 0) {
                            if (selectedIndexInList > playlist.getSongs().size()) {
                                selectedIndexInList = 1; // from the top
                            }
                            remainedTimeInEpisode +=
                                    playlist.getSongs().get(selectedIndexInList - 1).getDuration();
                            wrapper.updateSong(playlist.getSongs().get(selectedIndexInList - 1), 1, database, this);
                            this.songHistory.add(playlist.getSongs().get(selectedIndexInList - 1));
                            selectedIndexInList++;
                        }

                        selectedIndexInList--; // current song
                        timeRelativeToSong =
                                playlist.getSongs().get(selectedIndexInList - 1).getDuration()
                                        - remainedTimeInEpisode;
                        timeLoaded = timestamp;
                        return;
                    }
                    // shuffle and repeat playlist
                    int lastIndex = selectedIndexInList;
                    int shuffleIndex =
                            this.getShuffledIndexes().indexOf(selectedIndexInList - 1) + 1;
                    shuffleIndex++;
                    while (remainedTimeInEpisode <= 0) {
                        if (shuffleIndex > playlist.getSongs().size()) {
                            shuffleIndex = 1;
                        }
                        selectedIndexInList =
                                this.getShuffledIndexes().get(shuffleIndex - 1) + 1;
                        remainedTimeInEpisode +=
                                playlist.getSongs().get(selectedIndexInList - 1).getDuration();
                        wrapper.updateSong(playlist.getSongs().get(selectedIndexInList - 1), 1, database, this);
                        this.songHistory.add(playlist.getSongs().get(selectedIndexInList - 1));
                        lastIndex = selectedIndexInList;
                        shuffleIndex++;
                    }

                    selectedIndexInList = lastIndex;
                    timeRelativeToSong =
                            playlist.getSongs().get(selectedIndexInList - 1).getDuration()
                                    - remainedTimeInEpisode;
                    timeLoaded = timestamp;
                    return;
                }
                // playlist song infinitely
                int aux = timeRelativeToSong;
                timeRelativeToSong = (timeRelativeToSong + timestamp - timeLoaded)
                        % playlist.getSongs().get(selectedIndexInList - 1).getDuration();
                wrapper.updateSong(playlist.getSongs().get(selectedIndexInList - 1),
                        (aux + timestamp - timeLoaded)
                                / playlist.getSongs().get(selectedIndexInList - 1).getDuration(), database, this);
                this.timeLoaded = timestamp;
            }
            // playlist is on pause; nothing changes
        }
        // no playlist to simulate
    }

    /**
     * method simulates album up until given moment;
     * method checks if user was set on playing and then simulates album taking
     * into consideration repeat status and shuffle status;
     * if "No Repeat", we simulate songs one after another and if remaining time is still
     * negative it means album had ended and we set user on pause;
     * if "Repeat Current Song" we simulate current song being played continuously and update
     * the cursor and update time accordingly;
     * if "Repeat All" we simulate the album being played from the top and update current
     * song accordingly;
     * if shuffle is deactivated we consider the order the songs were introduced into the
     * album; otherwise we take into consideration the array of shuffled indexes
     * @param timestamp up to which we simulate
     * @param database extended input library
     */
    public void simulateAlbum(final int timestamp, final Database database) {
        if (selectedIndex > 0 && selectedIndex <= this.lastSearch.getResults().size()) {
            String albumName = this.lastSearch.getResults().get(selectedIndex - 1);
            Album album = database.findAlbum(albumName);

            if (album == null) {
                return;
            }

            if (playing) {
                if (selectedIndexInList < 1 || selectedIndexInList > album.getSongs().size()) {
                    return;
                }

                int remainedTimeInEpisode =
                        album.getSongs().get(selectedIndexInList - 1).getDuration()
                                - timeRelativeToSong;
                remainedTimeInEpisode = remainedTimeInEpisode - (timestamp - timeLoaded);
                if (remainedTimeInEpisode > 0) {
                    timeRelativeToSong =
                            album.getSongs().get(selectedIndexInList - 1).getDuration()
                                    - remainedTimeInEpisode;
                    timeLoaded = timestamp;
                    return;
                }
                if (repeat == 0) { // no repeat
                    if (!this.getShuffle()) {
                        selectedIndexInList++;
                        while (selectedIndexInList <= album.getSongs().size()
                                && remainedTimeInEpisode <= 0) {
                            remainedTimeInEpisode +=
                                    album.getSongs().get(selectedIndexInList - 1).getDuration();
                            wrapper.updateSong(album.getSongs().get(selectedIndexInList - 1), 1, database, this);
                            this.songHistory.add(album.getSongs().get(selectedIndexInList - 1));
                            selectedIndexInList++;
                        }
                        if (remainedTimeInEpisode < 0) { // playlist ended
                            selectedIndex = 0;
                            selectedIndexInList = 0;
                            playing = false;
                            timeLoaded = 0;
                            timeRelativeToSong = 0;
                            isLoaded = false;
                            isSelected = false;
                            return;
                        }
                        selectedIndexInList--; // current episode
                        timeRelativeToSong =
                                album.getSongs().get(selectedIndexInList - 1).getDuration()
                                        - remainedTimeInEpisode;
                        timeLoaded = timestamp;
                        return;
                    }
                    // shuffle but no repeat
                    int lastIndex = selectedIndexInList;
                    int shuffleIndex =
                            this.getShuffledIndexes().indexOf(selectedIndexInList - 1) + 1;
                    shuffleIndex++;
                    while (shuffleIndex <= album.getSongs().size()
                            && remainedTimeInEpisode <= 0) {
                        selectedIndexInList =
                                this.getShuffledIndexes().get(shuffleIndex - 1) + 1;
                        remainedTimeInEpisode +=
                                album.getSongs().get(selectedIndexInList - 1).getDuration();
                        wrapper.updateSong(album.getSongs().get(selectedIndexInList - 1), 1, database, this);
                        this.songHistory.add(album.getSongs().get(selectedIndexInList - 1));
                        lastIndex = selectedIndexInList;
                        shuffleIndex++;
                    }

                    if (remainedTimeInEpisode < 0) { // playlist ended
                        selectedIndex = 0;
                        selectedIndexInList = 0;
                        playing = false;
                        timeLoaded = 0;
                        timeRelativeToSong = 0;
                        isLoaded = false;
                        isSelected = false;
                        shuffle = false;
                        return;
                    }

                    selectedIndexInList = lastIndex;
                    timeRelativeToSong =
                            album.getSongs().get(selectedIndexInList - 1).getDuration()
                                    - remainedTimeInEpisode;
                    timeLoaded = timestamp;
                    return;
                }
                if (repeat == 1) { //repeat all
                    if (!this.getShuffle()) { // no shuffle
                        selectedIndexInList++;
                        while (remainedTimeInEpisode <= 0) {
                            if (selectedIndexInList > album.getSongs().size()) {
                                selectedIndexInList = 1; // from the top
                            }
                            remainedTimeInEpisode +=
                                    album.getSongs().get(selectedIndexInList - 1).getDuration();
                            wrapper.updateSong(album.getSongs().get(selectedIndexInList - 1), 1, database, this);
                            this.songHistory.add(album.getSongs().get(selectedIndexInList - 1));
                            selectedIndexInList++;
                        }

                        selectedIndexInList--; // current song
                        timeRelativeToSong =
                                album.getSongs().get(selectedIndexInList - 1).getDuration()
                                        - remainedTimeInEpisode;
                        timeLoaded = timestamp;
                        return;
                    }
                    // shuffle and repeat playlist
                    int lastIndex = selectedIndexInList;
                    int shuffleIndex =
                            this.getShuffledIndexes().indexOf(selectedIndexInList - 1) + 1;
                    shuffleIndex++;
                    while (remainedTimeInEpisode <= 0) {
                        if (shuffleIndex > album.getSongs().size()) {
                            shuffleIndex = 1;
                        }
                        selectedIndexInList =
                                this.getShuffledIndexes().get(shuffleIndex - 1) + 1;
                        remainedTimeInEpisode +=
                                album.getSongs().get(selectedIndexInList - 1).getDuration();
                        wrapper.updateSong(album.getSongs().get(selectedIndexInList - 1), 1, database, this);
                        this.songHistory.add(album.getSongs().get(selectedIndexInList - 1));
                        lastIndex = selectedIndexInList;
                        shuffleIndex++;
                    }

                    selectedIndexInList = lastIndex;
                    timeRelativeToSong =
                            album.getSongs().get(selectedIndexInList - 1).getDuration()
                                    - remainedTimeInEpisode;
                    timeLoaded = timestamp;
                    return;
                }
                // playlist song infinitely
                int aux = timeRelativeToSong;
                timeRelativeToSong = (timeRelativeToSong + timestamp - timeLoaded)
                        % album.getSongs().get(selectedIndexInList - 1).getDuration();
                wrapper.updateSong(album.getSongs().get(selectedIndexInList - 1),
                        (aux + timestamp - timeLoaded)
                        / album.getSongs().get(selectedIndexInList - 1).getDuration(), database, this);
                this.timeLoaded = timestamp;
            }
            // album is on pause; nothing changes
        }
        // no album to simulate
    }

    /**
     * method calls for specific methods regarding the type of the simulated audio file
     * @param timestamp up to which we simulate
     * @param database extended input library
     */
    public void simulate(final int timestamp, final Database database) {
        if (!this.isConnectionStatus()) { // if offline, we do not simulate
            return;
        }
        if (this.lastSearch == null) {
            return;
        }
        if (this.lastSearch.getType().equals("song")) {
            this.simulateSong(timestamp, database);
            return;
        }
        if (this.lastSearch.getType().equals("podcast")) {
            this.simulatePodcast(timestamp, database);
            return;
        }
        if (this.lastSearch.getType().equals("playlist")) {
            if (this.shuffle) {
                this.updateShuffleIndexesPlaylist(database);
            }
            this.simulatePlaylist(timestamp, database);
            return;
        }
        if (this.lastSearch.getType().equals("album")) {
            if (this.shuffle) {
                this.updateShuffleIndexesAlbum(database);
            }
            this.simulateAlbum(timestamp, database);
            return;
        }
    }


    /**
     * method updates shuffle indexes based on saved seed;
     * method is useful if playlist is altered (songs are removed or added)
     * @param database extended input library
     */
    public void updateShuffleIndexesPlaylist(final Database database) {
        String playlistName = this.getLoadedSourceName();
        if (playlistName.isEmpty()) {
            return;
        }
        Playlist playlist = database.findPlaylistInDatabase(playlistName);
        if (this.shuffledIndexes == null) {
            this.shuffledIndexes = new ArrayList<Integer>();
        } else {
            this.shuffledIndexes.clear();
        }
        Random generator = new Random(this.shuffleSeed);
        for (int i = 0; i < playlist.getSongs().size(); i++) {
            this.shuffledIndexes.add(i);
        }
        Collections.shuffle(this.shuffledIndexes, generator);
    }

    /**
     * method updates shuffle indexes based on saved seed;
     * method is useful if album is altered (songs are removed or added)
     * @param database extended input library
     */
    public void updateShuffleIndexesAlbum(final Database database) {
        String albumName = this.getLoadedSourceName();
        if (albumName.isEmpty()) {
            return;
        }
        Album album = database.findAlbum(albumName);
        if (this.shuffledIndexes == null) {
            this.shuffledIndexes = new ArrayList<Integer>();
        } else {
            this.shuffledIndexes.clear();
        }
        Random generator = new Random(this.shuffleSeed);
        for (int i = 0; i < album.getSongs().size(); i++) {
            this.shuffledIndexes.add(i);
        }
        Collections.shuffle(this.shuffledIndexes, generator);
    }

    public int getLoadedSourceId() {
        if (this.getLastSearch() == null) {
            return 0;
        }
        if (this.getSelectedIndex() < 1
                || this.getSelectedIndex() > this.getLastSearch().getResults().size()) {
            return 0;
        }
        return this.getLastSearch().getResultsId().get(this.selectedIndex - 1);
    }

    /**
     * method returns the name of loaded source, getting it from the last selection
     * @return name of loaded source
     */
    public String getLoadedSourceName() {
        if (this.getLastSearch() == null) {
            return "";
        }
        if (this.getSelectedIndex() < 1
                || this.getSelectedIndex() > this.getLastSearch().getResults().size()) {
            return "";
        }
        return this.getLastSearch().getResults().get(this.selectedIndex - 1);
    }
    /**
     * setter for playlist ID counter
     * @param playlistids new value for playlist ID counter
     */
    public void setPlaylistids(final int playlistids) {
        this.playlistids = playlistids;
    }
    /**
     * setter for followed playlist collection
     * @param followed new followed playlists list
     */
    public void setFollowed(final ArrayList<Playlist> followed) {
        this.followed = followed;
    }
    /**
     * setter for selected index in library
     * @param selectedIndexInLibrary new selected index in library
     */
    public void setSelectedIndexInLibrary(final int selectedIndexInLibrary) {
        this.selectedIndexInLibrary = selectedIndexInLibrary;
    }
    /**
     * getter for loaded status
     * @return loaded status
     */
    public boolean isLoaded() {
        return isLoaded;
    }
    /**
     * setter for loaded status
     * @param loaded new loaded status
     */
    public void setLoaded(final boolean loaded) {
        isLoaded = loaded;
    }
    /**
     * getter for selected status
     * @return selected status
     */
    public boolean getIsSelected() {
        return isSelected;
    }
    /**
     * setter for selected status
     * @param isSelected new selected status
     */
    public void setIsSelected(final boolean isSelected) {
        this.isSelected = isSelected;
    }
    /**
     * setter for type of the selected audio file
     * @param selectedType new selected type
     */
    public void setSelectedType(final String selectedType) {
        this.selectedType = selectedType;
    }
    /**
     * getter for shuffle status
     * @return shuffle status
     */
    public boolean getShuffle() {
        return shuffle;
    }
    /**
     * setter for shuffle status
     * @param shuffle new shuffle status
     */
    public void setShuffle(final boolean shuffle) {
        this.shuffle = shuffle;
    }
    /**
     * setter for repeat status
     * @param repeat new repeat status
     */
    public void setRepeat(final int repeat) {
        this.repeat = repeat;
    }
    /**
     * setter for username
     * @param username new username
     */
    public void setUsername(final String username) {
        this.username = username;
    }
    /**
     * setter for user's last search
     * @param lastSearch new last search information
     */
    public void setLastSearch(final SearchBar lastSearch) {
        this.lastSearch = lastSearch;
        this.selectedIndex = 1;
        this.timeLoaded = 0;
    }
    /**
     * setter for selected index in search results
     * @param selectedIndex new selected index
     */
    public void setSelectedIndex(final int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    /**
     * setter for selected index in podcast/playlist
     * @param selectedIndexInList new index
     */
    public void setSelectedIndexInList(final int selectedIndexInList) {
        this.selectedIndexInList = selectedIndexInList;
    }
    /**
     * setter for playing status
     * @param playing new playing status
     */
    public void setPlaying(final boolean playing) {
        this.playing = playing;
    }
    /**
     * setter for the time of last update
     * @param timeLoaded new time of update
     */
    public void setTimeLoaded(final int timeLoaded) {
        this.timeLoaded = timeLoaded;
    }
    /**
     * setter for time cursor relative to audio file
     * @param timeRelativeToSong new time cursor
     */
    public void setTimeRelativeToSong(final int timeRelativeToSong) {
        this.timeRelativeToSong = timeRelativeToSong;
    }
    /**
     * setter for current page
     * @param currentPage new current page
     */
    public void setCurrentPage(final Page currentPage) {
        this.currentPage = currentPage;
    }
    /**
     * setter for shuffled indexes
     * @param shuffledIndexes new shuffled indexes
     */
    public void setShuffledIndexes(final ArrayList<Integer> shuffledIndexes) {
        this.shuffledIndexes = shuffledIndexes;
    }
    /**
     * setter for shuffle seed
     * @param shuffleSeed new shuffle seed
     */
    public void setShuffleSeed(final int shuffleSeed) {
        this.shuffleSeed = shuffleSeed;
    }
    /**
     * setter for age
     * @param age new age
     */
    public void setAge(final int age) {
        this.age = age;
    }
    /**
     * setter for city
     * @param city new city
     */
    public void setCity(final String city) {
        this.city = city;
    }
    /**
     * setter for connection status
     * @param connectionStatus new connection status
     */
    public void setConnectionStatus(final boolean connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public void setPremium(final boolean premium) {
        this.premium = premium;
    }
}
