package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.Playlist;
import main.user.User;

public final class Follow extends Command {
    private String message;
    public Follow(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method prints corresponding error message;
     * method checks if user was already following given playlist; if yes,
     * user is set to unfollow playlist; otherwise user is set to follow playlist;
     * playlist is added/removed from user's followed playlists collection;
     * playlist's number of followers is increased/decreased
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (!user.getIsSelected()) {
            this.message = "Please select a source before following or unfollowing.";
            return;
        }
        if (!user.getLastSearch().getType().equals("playlist")) {
            this.message = "The selected source is not a playlist.";
            return;
        }
        if (user.getSelectedIndex() < 1
                || user.getSelectedIndex() > user.getLastSearch().getResults().size()) {
            return;
        }
        String playlistName = user.getLastSearch().getResults().get(user.getSelectedIndex() - 1);
        Playlist playlist = database.findPlaylistInDatabase(playlistName);
        if (playlist.getUser().getUsername().equals(this.getUsername())) {
            this.message = "You cannot follow or unfollow your own playlist.";
            return;
        }
        if (user.findFollowedPlaylist(playlist)) {
            user.unfollowPlaylist(playlist);
            playlist.setFollowers(playlist.getFollowers() - 1);
            this.message = "Playlist unfollowed successfully.";
            return;
        }
        user.followPlaylist(playlist);
        playlist.setFollowers(playlist.getFollowers() + 1);
        this.message = "Playlist followed successfully.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
