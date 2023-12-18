package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.Album;
import main.Playlist;
import main.user.User;

public final class Next extends Command {
    private String message;
    public Next(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sets user to be playing next song regarding repeat status;
     * if "No Repeat" set, user is set on pause;
     * if "Repeat Once" set, next song is current song and repeat status is set on "No Repeat";
     * if "Repeat Infinitely", next song is current song and repeat status remains unchanged;
     * @param database extended input library
     * @param user user that gave next command
     */
    private void nextSong(final Database database, final User user) {
        if (user.getSelectedIndex() == 0
                || user.getSelectedIndex() > user.getLastSearch().getResults().size()) {
            this.message = "Please load a source before skipping to the next track.";
            return;
        }
        SongInput song = user.getSongFromUser(database);
        if (song == null) {
            this.message = "Please load a source before skipping to the next track.";
            return;
        }
        if (user.getRepeat() == 1) { // repeat once
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setRepeat(0);
            user.setPlaying(true);
            this.message = "Skipped to next track successfully. The current track is "
                    + song.getName() + ".";
            return;
        }
        if (user.getRepeat() == 2) { // repeat infinitely
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setPlaying(true);
            this.message = "Skipped to next track successfully. The current track is "
                    + song.getName() + ".";
            return;
        }
        // no repeat; and stop
        this.message = "Please load a source before skipping to the next track.";
        user.setLoaded(false);
        user.setIsSelected(false);
        user.setTimeLoaded(0);
        user.setSelectedIndex(0);
        user.setTimeRelativeToSong(0);
        user.setSelectedIndexInList(1);
        user.setPlaying(false);
    }

    /**
     * method sets user to be playing next song in playlist regarding repeat status;
     * if playlist is not shuffled, we use the order the songs were introduced in the playlist;
     * if playlist is shuffled, we use the order given through the array of indexes;
     * if "No Repeat", next song is the song from next position in playlist;
     * if "Repeat Current Song", next song is current song;
     * if next song is last song of playlist, if "No Repeat" the user is set on pause, if
     * "Repeat All", playlist is reloaded from the top
     * @param database extended input library
     * @param user user that gave next command
     */
    private void nextPlaylist(final Database database, final User user) {
        if (user.getSelectedIndex() < 1
                || user.getSelectedIndex() > user.getLastSearch().getResults().size()) {
            return;
        }
        String playlistName = user.getLastSearch().getResults().get(user.getSelectedIndex() - 1);
        Playlist playlist = user.findPlaylist(playlistName);
        if (playlist == null) {
            return;
        }
        if (playlist.getSongs().isEmpty()) {
            return;
        }
        if (!user.getShuffle()) {
            if (user.getRepeat() == 2) { // repeat current song
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                user.setPlaying(true);
                this.message = "Skipped to next track successfully. The current track is "
                        + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            int index = user.getSelectedIndexInList() + 1;
            if (index <= playlist.getSongs().size()) {
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                user.setSelectedIndexInList(index);
                user.setPlaying(true);
                this.message = "Skipped to next track successfully. The current track is "
                        + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            if (user.getRepeat() == 1) { // repeat all
                index = 1;
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                user.setSelectedIndexInList(index);
                user.setPlaying(true);
                this.message = "Skipped to next track successfully. The current track is "
                        + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            // no repeat, playlist ended
            user.setSelectedIndex(0);
            user.setSelectedIndexInList(1);
            user.setLoaded(false);
            user.setPlaying(false);
            user.setTimeRelativeToSong(0);
            this.message = "Please load a source before skipping to the next track.";
            return;
        }

        // shuffle
        if (user.getRepeat() == 2) { // repeat current song
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setPlaying(true);
            this.message = "Skipped to next track successfully. The current track is "
                    + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        int indexShuffle =
                user.getShuffledIndexes().indexOf(user.getSelectedIndexInList() - 1) + 1;
        indexShuffle++;
        if (indexShuffle <= playlist.getSongs().size()) {
            user.setPlaying(true);
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setSelectedIndexInList(user.getShuffledIndexes().get(indexShuffle - 1) + 1);
            this.message = "Skipped to next track successfully. The current track is "
                    + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        if (user.getRepeat() == 1) { // repeat all
            indexShuffle = 1;
            user.setPlaying(true);
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setSelectedIndexInList(user.getShuffledIndexes().get(0) + 1);
            this.message = "Skipped to next track successfully. The current track is "
                    + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        // no repeat, playlist ended
        user.setSelectedIndex(0);
        user.setSelectedIndexInList(1);
        user.setLoaded(false);
        user.setPlaying(false);
        user.setTimeRelativeToSong(0);
        this.message = "Please load a source before skipping to the next track.";
        return;
    }

    private void nextAlbum(final Database database, final User user) {
        if (user.getSelectedIndex() < 1
                || user.getSelectedIndex() > user.getLastSearch().getResults().size()) {
            return;
        }
        String albumName = user.getLastSearch().getResults().get(user.getSelectedIndex() - 1);
        Album album = database.findAlbum(albumName);
        if (album == null) {
            return;
        }
        if (album.getSongs().isEmpty()) {
            return;
        }
        if (!user.getShuffle()) {
            if (user.getRepeat() == 2) { // repeat current song
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                user.setPlaying(true);
                this.message = "Skipped to next track successfully. The current track is "
                        + album.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            int index = user.getSelectedIndexInList() + 1;
            if (index <= album.getSongs().size()) {
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                user.setSelectedIndexInList(index);
                user.setPlaying(true);
                this.message = "Skipped to next track successfully. The current track is "
                        + album.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            if (user.getRepeat() == 1) { // repeat all
                index = 1;
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                user.setSelectedIndexInList(index);
                user.setPlaying(true);
                this.message = "Skipped to next track successfully. The current track is "
                        + album.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            // no repeat, album ended
            user.setSelectedIndex(0);
            user.setSelectedIndexInList(1);
            user.setLoaded(false);
            user.setPlaying(false);
            user.setTimeRelativeToSong(0);
            this.message = "Please load a source before skipping to the next track.";
            return;
        }

        // shuffle
        if (user.getRepeat() == 2) { // repeat current song
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setPlaying(true);
            this.message = "Skipped to next track successfully. The current track is "
                    + album.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        int indexShuffle =
                user.getShuffledIndexes().indexOf(user.getSelectedIndexInList() - 1) + 1;
        indexShuffle++;
        if (indexShuffle <= album.getSongs().size()) {
            user.setPlaying(true);
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setSelectedIndexInList(user.getShuffledIndexes().get(indexShuffle - 1) + 1);
            this.message = "Skipped to next track successfully. The current track is "
                    + album.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        if (user.getRepeat() == 1) { // repeat all
            indexShuffle = 1;
            user.setPlaying(true);
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setSelectedIndexInList(user.getShuffledIndexes().get(0) + 1);
            this.message = "Skipped to next track successfully. The current track is "
                    + album.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        // no repeat, playlist ended
        user.setSelectedIndex(0);
        user.setSelectedIndexInList(1);
        user.setLoaded(false);
        user.setPlaying(false);
        user.setTimeRelativeToSong(0);
        this.message = "Please load a source before skipping to the next track.";
        return;
    }



    /**
     * method sets user to be playing next episode in podcast regarding repeat status;
     * if "No Repeat", next episode becomes the episode following; if nonexistent,
     * user is set on pause;
     * if "Repeat Once", current episode is set to be played once more and repeat status is set
     * to "No Repeat"
     * if "Repeat Infinitely", current episode is set to be played once more and repeat status
     * remains unchanged
     * @param database extended library input
     * @param user user that gave next command
     */
    private void nextPodcast(final Database database, final User user) {
        if (user.getSelectedIndex() < 1
                || user.getSelectedIndex() > user.getLastSearch().getResults().size()) {
            return;
        }
        String podcastName = user.getLastSearch().getResults().get(user.getSelectedIndex() - 1);
        PodcastInput podcast = database.findPodcast(podcastName);
        if (podcast == null) {
            return;
        }
        if (user.getRepeat() == 1) {
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setRepeat(0);
            user.setPlaying(true);
            this.message = "Skipped to next track successfully. The current track is "
                    + podcast.getEpisodes().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        if (user.getRepeat() == 2) {
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setPlaying(true);
            this.message = "Skipped to next track successfully. The current track is "
                    + podcast.getEpisodes().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        int index = user.getSelectedIndexInList() + 1;
        if (index <= podcast.getEpisodes().size()) {
            user.setSelectedIndexInList(index);
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setPlaying(true);
            this.message = "Skipped to next track successfully. The current track is "
                    + podcast.getEpisodes().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        // podcast ended, no repeat
        user.setSelectedIndex(0);
        user.setSelectedIndexInList(1);
        user.setLoaded(false);
        user.setPlaying(false);
        user.setTimeRelativeToSong(0);
        this.message = "Please load a source before skipping to the next track.";
    }

    /**
     * method sets corresponding error messages;
     * simulation is performed up until this point;
     * regarding the type of loaded audio file, method calls specific next methods;
     * note that if user was set on pause, after next command user is set on playing
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (!user.isLoaded()) {
            this.message = "Please load a source before skipping to the next track.";
            return;
        }
        user.simulate(this.getTimestamp(), database);
        if (!user.isLoaded()) {
            this.message = "Please load a source before skipping to the next track.";
            return;
        }

        if (user.getSelectedType().equals("song")) {
            this.nextSong(database, user);
            return;
        }
        if (user.getSelectedType().equals("playlist")) {
            if (user.getShuffle()) {
                user.updateShuffleIndexesPlaylist(database);
            }
            this.nextPlaylist(database, user);
            return;
        }
        if (user.getSelectedType().equals("podcast")) {
            this.nextPodcast(database, user);
            return;
        }
        if (user.getSelectedType().equals("album")) {
            if (user.getShuffle()) {
                user.updateShuffleIndexesAlbum(database);
            }
            this.nextAlbum(database, user);
            return;
        }
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
