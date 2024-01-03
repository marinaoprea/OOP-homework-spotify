package commands.show;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.Album;
import main.user.Artist;
import main.user.User;

import java.util.ArrayList;

public final class ShowAlbums extends Command {
    private ArrayList<Album> albums;
    public ShowAlbums(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method gets an artist's albums collection and sets it as command result
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        Artist artist = (Artist) user;
        albums = artist.getAlbums();
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode resultAlbums = mapper.createArrayNode();
        for (Album album : albums) {
            ObjectNode objectNodeAlbum = mapper.createObjectNode();
            objectNodeAlbum.put("name", album.getName());

            ArrayNode albumSongs = mapper.createArrayNode();
            for (SongInput song : album.getSongs()) {
                albumSongs.add(song.getName());
            }
            objectNodeAlbum.put("songs", albumSongs);
            resultAlbums.add(objectNodeAlbum);
        }
        objectNode.put("result", resultAlbums);
    }
}
