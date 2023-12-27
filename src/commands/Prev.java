package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import main.CommandInput;
import main.Playlist;
import main.Album;
import main.Database;
import main.user.User;

public final class Prev extends Command {
    private String message;
    public Prev(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method checks if the cursor on the current song is not at the beginning;
     * in this case, previous song is current song and user is set to play it once more;
     * otherwise, we set previous song regarding repeat status;
     * if "No Repeat", user is set on pause;
     * if "Repeat Once", previous song is current song played once more and repeat status
     * is set on "No Repeat";
     * if "Repeat Infinitely", previous song is current song played once more and repeat
     * status remains unchanged
     * @param database extended library input
     * @param user user that gave prev command
     */
    private void prevSong(final Database database, final User user) {
        if (user.getSelectedIndex() < 1
                || user.getSelectedIndex() > user.getLastSearch().getResults().size()) {
            this.message = "Please load a source before returning to the previous track.";
            return;
        }
        SongInput song = user.getSongFromUser(database);
        if (song == null) {
            return;
        }
        user.setPlaying(true);
        if (user.getTimeRelativeToSong() > 0) {
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            this.message = "Returned to the previous track successfully. The current track is "
                    + song.getName() + ".";
            return;
        }
        if (user.getRepeat() == 1) { // repeat once
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setRepeat(0);
            this.message = "Returned to previous track successfully. The current track is "
                    + song.getName() + ".";
            return;
        }
        if (user.getRepeat() == 2) { // repeat infinitely
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            this.message = "Returned to previous track successfully. The current track is "
                    + song.getName() + ".";
            return;
        }

        user.setTimeLoaded(this.getTimestamp());
        user.setTimeRelativeToSong(0);
        this.message = "Returned to previous track successfully. The current track is "
                + song.getName() + ".";
    }

    /**
     * method checks if the cursor on the current song in playlist is set at the
     * beginning; in this case, previous song is current song played once more;
     * otherwise, we set previous song regarding repeat status and shuffle status;
     * if not shuffled, we use the order the songs were introduced into the playlist;
     * if shuffled, we use the order given in the shuffled array of indexes;
     * @param database extended library input
     * @param user user that gave prev command
     */
    private void prevPlaylist(final Database database, final User user) {
        if (user.getSelectedIndex() < 1
                || user.getSelectedIndex() > user.getLastSearch().getResults().size()) {
            return;
        }
        String playlistName = user.getLastSearch().getResults().get(user.getSelectedIndex() - 1);
        Playlist playlist = user.findPlaylist(playlistName);
        user.setPlaying(true);
        if (playlist == null) {
            return;
        }
        if (playlist.getSongs().isEmpty()) {
            return;
        }
        if (user.getTimeRelativeToSong() > 0) {
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            this.message = "Returned to previous track successfully. The current track is "
                    + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        if (!user.getShuffle()) {
            if (user.getRepeat() == 2) { // repeat current song
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                this.message = "Returned to previous track successfully. The current track is "
                        + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            int index = user.getSelectedIndexInList() - 1;
            if (index >= 1) {
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                user.setSelectedIndexInList(index);
                this.message = "Returned to previous track successfully. The current track is "
                        + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            if (user.getRepeat() == 1) { // repeat all
                index = 1;
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                user.setSelectedIndexInList(index);
                this.message = "Returned to previous track successfully. The current track is "
                        + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            // no repeat, playlist ended
            user.setTimeLoaded(this.getTimestamp());
            user.setTimeRelativeToSong(0);
            user.setSelectedIndexInList(1);
            this.message = "Returned to previous track successfully. The current track is "
                    + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        int indexShuffle =
                user.getShuffledIndexes().indexOf(user.getSelectedIndexInList() - 1) + 1;
        indexShuffle--;
        if (indexShuffle >= 1) {
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setSelectedIndexInList(user.getShuffledIndexes().get(indexShuffle - 1) + 1);
            this.message = "Returned to previous track successfully. The current track is "
                    + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        indexShuffle = 1;
        user.setTimeRelativeToSong(0);
        user.setTimeLoaded(this.getTimestamp());
        user.setSelectedIndexInList(user.getShuffledIndexes().get(indexShuffle - 1) + 1);
        this.message = "Returned to previous track successfully. The current track is "
                + playlist.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
    }

    private void prevAlbum(final Database database, final User user) {
        if (user.getSelectedIndex() < 1
                || user.getSelectedIndex() > user.getLastSearch().getResults().size()) {
            return;
        }
        String albumName = user.getLastSearch().getResults().get(user.getSelectedIndex() - 1);
        Album album = database.findAlbum(albumName, user.getLoadedSourceId());
        user.setPlaying(true);
        if (album == null) {
            return;
        }
        if (album.getSongs().isEmpty()) {
            return;
        }
        if (user.getTimeRelativeToSong() > 0) {
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            this.message = "Returned to previous track successfully. The current track is "
                    + album.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        if (!user.getShuffle()) {
            if (user.getRepeat() == 2) { // repeat current song
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                this.message = "Returned to previous track successfully. The current track is "
                        + album.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            int index = user.getSelectedIndexInList() - 1;
            if (index >= 1) {
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                user.setSelectedIndexInList(index);
                this.message = "Returned to previous track successfully. The current track is "
                        + album.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            if (user.getRepeat() == 1) { // repeat all
                index = 1;
                user.setTimeRelativeToSong(0);
                user.setTimeLoaded(this.getTimestamp());
                user.setSelectedIndexInList(index);
                this.message = "Returned to previous track successfully. The current track is "
                        + album.getSongs().get(user.getSelectedIndexInList() - 1).getName()
                        + ".";
                return;
            }

            // no repeat, playlist ended
            user.setTimeLoaded(this.getTimestamp());
            user.setTimeRelativeToSong(0);
            user.setSelectedIndexInList(1);
            this.message = "Returned to previous track successfully. The current track is "
                    + album.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        int indexShuffle =
                user.getShuffledIndexes().indexOf(user.getSelectedIndexInList() - 1) + 1;
        indexShuffle--;
        if (indexShuffle >= 1) {
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setSelectedIndexInList(user.getShuffledIndexes().get(indexShuffle - 1) + 1);
            this.message = "Returned to previous track successfully. The current track is "
                    + album.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        indexShuffle = 1;
        user.setTimeRelativeToSong(0);
        user.setTimeLoaded(this.getTimestamp());
        user.setSelectedIndexInList(user.getShuffledIndexes().get(indexShuffle - 1) + 1);
        this.message = "Returned to previous track successfully. The current track is "
                + album.getSongs().get(user.getSelectedIndexInList() - 1).getName() + ".";
    }


    /**
     * method checks if the cursor on the current episode in podcast is set at
     * the beginning; in this case previous episode is current episode;
     * otherwise previous episode is set regarding repeat status;
     * if "No Repeat", we go to previous episode in episode list; if nonexistent,
     * user is set on pause;
     * if "Repeat Once", previous episode is current episode and repeat status
     * is set on "No Repeat";
     * if "Repeat Infinite", previous episode is current episode and repeat status
     * remains unchanged;
     * @param database extended library input
     * @param user user that gave prev command
     */
    private void prevPodcast(final Database database, final User user) {
        if (user.getSelectedIndex() < 1
                || user.getSelectedIndex() > user.getLastSearch().getResults().size()) {
            return;
        }
        String podcastName = user.getLastSearch().getResults().get(user.getSelectedIndex() - 1);
        PodcastInput podcast = database.findPodcast(podcastName);
        if (podcast == null) {
            return;
        }
        if (podcast.getEpisodes().isEmpty()) {
            return;
        }

        user.setPlaying(true);
        if (user.getTimeRelativeToSong() > 0) {
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            this.message = "Returned to previous track successfully. Current track is "
                    + podcast.getEpisodes().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        if (user.getRepeat() == 1) {
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setRepeat(0);
            this.message = "Returned to previous track successfully. The current track is "
                    + podcast.getEpisodes().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        if (user.getRepeat() == 2) {
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            this.message = "Returned to previous track successfully. The current track is "
                    + podcast.getEpisodes().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        int index = user.getSelectedIndexInList() - 1;
        if (index >= 1) {
            user.setSelectedIndexInList(index);
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            this.message = "Returned to previous track successfully. The current track is "
                    + podcast.getEpisodes().get(user.getSelectedIndexInList() - 1).getName() + ".";
            return;
        }

        // podcast ended, no repeat
        user.setSelectedIndexInList(1);
        user.setTimeRelativeToSong(0);
        user.setTimeLoaded(this.getTimestamp());
    }

    /**
     * method sets corresponding error messages;
     * method calls for simulation up until this point;
     * method calls for specific prev methods regarding the type of the loaded
     * audio file
     * note that if user was set on pause, after next command user is set on playing
     * @param database input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (!user.isLoaded()) {
            this.message = "Please load a source before returning to the previous track.";
            return;
        }
        user.simulate(this.getTimestamp(), database);
        if (!user.isLoaded()) {
            this.message = "Please load a source before returning to the previous track.";
            return;
        }

        if (user.getSelectedType().equals("song")) {
            this.prevSong(database, user);
            return;
        }
        if (user.getSelectedType().equals("playlist")) {
            if (user.getShuffle()) {
                user.updateShuffleIndexesPlaylist(database);
            }
            this.prevPlaylist(database, user);
            return;
        }
        if (user.getSelectedType().equals("podcast")) {
            this.prevPodcast(database, user);
            return;
        }
        if (user.getSelectedType().equals("album")) {
            if (user.getShuffle()) {
                user.updateShuffleIndexesAlbum(database);
            }
            this.prevAlbum(database, user);
            return;
        }
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
