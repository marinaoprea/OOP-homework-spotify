package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.Playlist;
import main.user.User;

public final class SwitchVisibility extends Command {
    private String message;
    private int playlistId;
    public SwitchVisibility(final CommandInput commandInput) {
        super(commandInput);
        this.playlistId = commandInput.getPlaylistId();
    }

    /**
     * method sets corresponding error messages;
     * method checks visibility of playlist;
     * if private, playlist is set on public, otherwise playlist
     * is set on private
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        Playlist playlist = user.findPlaylist(this.playlistId);
        if (playlist == null) {
            this.message = "The specified playlist ID is too high.";
            return;
        }
        playlist.setVisibility(!playlist.isVisibility());
        if (playlist.isVisibility()) {
            this.message = "Visibility status updated successfully to private.";
        } else {
            this.message = "Visibility status updated successfully to public.";
        }
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
