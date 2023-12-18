package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.Playlist;
import main.user.User;

public final class AddRemoveInPlaylist extends Command {
    private int playlistID;
    private String message;
    public AddRemoveInPlaylist(final CommandInput commandInput) {
        super(commandInput);
        this.playlistID = commandInput.getPlaylistId();
    }

    /**
     * method prints corresponding error messages;
     * method checks if song already exists in playlist with given ID;
     * if true, song is removed from playlist, otherwise song is added to the
     * playlist
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "User does not exist.";
            return;
        }
        if (user.getTimeLoaded() == 0) {
            this.message = "Please load a source before adding to or removing from the playlist.";
            return;
        }
        user.simulate(this.getTimestamp(), database);
        if (!user.getSelectedType().equals("song") && !user.getSelectedType().equals("album")) {
            this.message = "The loaded source is not a song.";
            return;
        }
        Playlist playlist = user.findPlaylist(this.playlistID);
        if (playlist == null) {
            this.message = "The specified playlist does not exist.";
            return;
        }
        SongInput song;
        if (user.getSelectedType().equals("song")) {
            song = user.getSongFromUser(database);
        } else {
            song = user.getSongFromUserInAlbum(database);
        }
        if (song == null) {
            return;
        }
        if (!playlist.findSong(song)) {
            playlist.addSong(song);
            this.message = "Successfully added to playlist.";
            return;
        }
        playlist.removeSong(song);
        this.message = "Successfully removed from playlist.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
