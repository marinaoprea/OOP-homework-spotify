package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import main.Album;
import main.CommandInput;
import main.Database;
import main.Playlist;
import main.user.User;

public final class Load extends Command {
    private String message;
    public Load(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method prints specific error messages and marks user as having loaded what had been
     * selected; in case of podcast being previously loaded, we save current state in user history;
     * in case of podcast being loaded we restore podcast to saved in history state
     * @param database input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "User does not exist.";
            return;
        }
        if (user.getSelectedIndex() == 0 || !user.getIsSelected()) {
            this.message = "Please select a source before attempting to load.";
            return;
        }
        if (user.getLastSearch() == null) {
            this.message = "You can't load an empty audio collection!";
            return;
        }
        this.message = "Playback loaded successfully.";

        user.setRepeat(0);
        user.setIsSelected(false);
        user.setLoaded(true);
        //System.out.println("bla");
        if (user.getLastSearch().getType().equals("song")) {
            user.setSelectedType("song");
            user.setTimeLoaded(this.getTimestamp());
            user.setPlaying(true);
            user.setSelectedIndexInList(1);
            user.setTimeRelativeToSong(0);
            SongInput song = database.findSong(user.getLoadedSourceName(), user.getLoadedSourceId());
            user.getWrapper().updateSong(song, 1, database, user);
            user.getSongHistory().updateSong(song, 1, database, user);
            return;
        }
        if (user.getLastSearch().getType().equals("playlist")) {
            user.setSelectedType("playlist");
            user.setSelectedIndexInList(1);
            user.setPlaying(true);
            user.setTimeRelativeToSong(0);
            user.setShuffle(false);
            user.setTimeLoaded(this.getTimestamp());

            Playlist playlist = database.findPlaylistInDatabase(user.getLoadedSourceName());
            SongInput song = playlist.getSongs().get(0);
            user.getWrapper().updateSong(song, 1, database, user);
            user.getSongHistory().updateSong(song, 1, database, user);
            return;
        }
        if (user.getLastSearch().getType().equals("podcast")) {
            user.setSelectedType("podcast");
            user.setPlaying(true);
            user.setTimeLoaded(this.getTimestamp());
            String podcastName = user.getLastSearch().getResults().get(user.getSelectedIndex() - 1);
            user.restorePodcast(podcastName);
            PodcastInput podcastInput = database.findPodcast(podcastName);
            EpisodeInput episodeInput = podcastInput.getEpisodes().get(user.getSelectedIndexInList() - 1);
            user.getWrapper().updatePodcasts(podcastInput, episodeInput, 1, database, user);
            return;
        }
        if (user.getLastSearch().getType().equals("album")) {
            user.setSelectedType("album");
            user.setShuffle(false);
            user.setPlaying(true);
            user.setTimeLoaded(this.getTimestamp());
            user.setSelectedIndexInList(1);
            user.setTimeRelativeToSong(0);
            Album album = database.findAlbum(user.getLoadedSourceName(), user.getLoadedSourceId());
            SongInput song = album.getSongs().get(0);
            user.getWrapper().updateSong(song, 1, database, user);
            user.getSongHistory().updateSong(song, 1, database, user);
            return;
        }
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
