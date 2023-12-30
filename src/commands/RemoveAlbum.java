package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.Album;
import main.user.Artist;
import main.user.User;

public final class RemoveAlbum extends Command {
    private String message;
    private final String name;
    public RemoveAlbum(final CommandInput commandInput) {
        super(commandInput);
        this.name = commandInput.getName();
    }

    /**
     * method sets corresponding error messages;
     * method checks if user exists, if user's an artist, if user has an album
     * with given name;
     * if valid, method checks if album can be deleted; if so, method calls album
     * clearance method and then removes album from artist's list and from global
     * list of albums in database
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (!database.findArtistByName(this.getUsername())) {
            this.message = this.getUsername() + " is not an artist.";
            return;
        }
        Artist artist = (Artist) user;
        Album album = artist.getAlbumByName(this.name);
        if (album == null) {
            this.message = this.getUsername() + " doesn't have an album with the given name.";
            return;
        }
        if (!album.checkDelete(this.getTimestamp(), database)) {
            this.message = this.getUsername() + " can't delete this album.";
            return;
        }
        this.message = this.getUsername() + " deleted the album successfully.";
        album.clearSongs(database);
        artist.getAlbums().remove(album);
        database.removeAlbum(album);
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
