package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.Playlist;
import main.Recommendation;
import main.user.User;

public class LoadRecommendations extends Command {
    private String message;
    public LoadRecommendations(final CommandInput commandInput) {
        super(commandInput);
    }

    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (!user.isConnectionStatus()) {
            this.message = this.getUsername() + " is offline.";
            return;
        }
        if (user.getHomePage().getRecommendations().isEmpty()) {
            this.message = "No recommendations available.";
            return;
        }
        user.simulate(this.getTimestamp(), database);

        Recommendation lastRecommendation = user.getHomePage().getRecommendations().getLast();
        if (lastRecommendation.getType().equals("song")) {
            user.setSelectedType("song");
            user.setIsSelected(false);
            user.setLoaded(true);
            user.setRepeat(0);
            user.setTimeLoaded(this.getTimestamp());
            user.setPlaying(true);
            user.setSelectedIndexInList(1);
            user.setTimeRelativeToSong(0);
            SongInput song = (SongInput)lastRecommendation;
            user.getWrapper().updateSong(song, 1, database, user);
            user.getSongHistory().updateSong(song, 1, database, user);

            user.getLastSearch().getResults().set(0, song.getName());
            user.getLastSearch().getResultsId().set(0, song.getId());
            user.setSelectedIndex(1);
            this.message = "Playback loaded successfully.";
            return;
        }
       /* if (lastRecommendation.getType().equals("playlist")) {
            user.setRepeat(0);
            user.setIsSelected(false);
            user.setLoaded(true);
            user.setSelectedType("playlist");
            user.setSelectedIndexInList(1);
            user.setPlaying(true);
            user.setTimeRelativeToSong(0);
            user.setShuffle(false);
            user.setTimeLoaded(this.getTimestamp());

            Playlist playlist = (Playlist)lastRecommendation;
            database.getGlobalPlaylists().add(playlist);
            user.getLastSearch().getResults().set(0, playlist.getName());
            user.setSelectedIndex(1);
            SongInput song = playlist.getSongs().get(0);
            user.getWrapper().updateSong(song, 1, database, user);
            user.getSongHistory().updateSong(song, 1, database, user);
        }*/
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
