package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.Playlist;
import main.user.User;

public final class CreatePlaylist extends Command {
    private String message;
    private final String playlistName;
    public CreatePlaylist(final CommandInput commandInput) {
        super(commandInput);
        this.playlistName = commandInput.getPlaylistName();
    }

    /**
     * method prints corresponding error messages;
     * method checks if a playlist with given name already exists;
     * if not, new playlist is created and added in user's playlist
     * collection
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "User does not exist.";
            return;
        }
        Playlist playlist = user.findPlaylist(this.playlistName);
        if (playlist != null) {
            this.message = "A playlist with the same name already exists.";
            return;
        }
        playlist = new Playlist(user, this.playlistName, false);
        user.getMyPlaylists().add(playlist);
        database.getGlobalPlaylists().add(playlist);
        playlist.setTimestamp(this.getTimestamp());
        this.message = "Playlist created successfully.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}

