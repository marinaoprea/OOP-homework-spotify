package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.Album;
import main.user.User;
import main.user.Artist;
import main.Notification;

import java.util.ArrayList;

public final class AddAlbum extends Command {
    private String message;
    private final String name;
    private final int releaseYear;
    private final String description;
    private final ArrayList<SongInput> songs;
    public AddAlbum(final CommandInput commandInput) {
        super(commandInput);
        this.description = commandInput.getDescription();
        this.releaseYear = commandInput.getReleaseYear();
        this.name = commandInput.getName();
        this.songs = commandInput.getSongs();
    }

    /**
     * method sets corresponding error message;
     * method checks if user is artist, if he doesn't already have an album with
     * the same name and if album is valid (does not contain two songs with same name);
     * method adds new album in artist's album list and in total albums list in database
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (!database.findArtistByName(user.getUsername())) {
            this.message = this.getUsername() + " is not an artist.";
            return;
        }
        //Artist artist = (Artist) user;
        Artist artist = database.findArtist(this.getUsername());
        if (artist.checkAlbumByName(this.name)) {
            this.message = this.getUsername() + " has another album with the same name.";
            return;
        }
        if (songs.stream().map(SongInput::getName).distinct().count() != songs.size()) {
            this.message = this.getUsername() + " has the same song at least twice in this album.";
            return;
        }
        Album newAlbum = new Album(name, releaseYear, description, artist, songs);
        for (SongInput song : songs) {
            database.getSongs().add(song);

            int oldId = database.getSongId();
            song.setId(oldId + 1);
            database.setSongId(oldId + 1);

            database.getNoLikesPerSong().add(0);
        }
        database.getAlbums().add(newAlbum);
        artist.getAlbums().add(newAlbum);

        Notification notification = new Notification("Album", artist);
        artist.notify(notification);

        this.message = this.getUsername() + " has added new album successfully.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
