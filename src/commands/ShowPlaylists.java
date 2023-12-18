package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.Playlist;
import main.user.User;

import java.util.ArrayList;

public final class ShowPlaylists extends Command {
    private ArrayList<Playlist> result;
    public ShowPlaylists(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method gets a user's playlist collection and sets it as command result
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        this.result = user.getMyPlaylists();
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode playlistList = mapper.createArrayNode();
        if (!this.result.isEmpty()) {
            for (Playlist aux : this.result) {
                ObjectNode playlistObj = mapper.createObjectNode();
                playlistObj.put("name", aux.getName());
                ArrayNode songsArr = mapper.createArrayNode();
                for (SongInput song : aux.getSongs()) {
                    songsArr.add(song.getName());
                }
                playlistObj.put("songs", songsArr);
                if (aux.isVisibility()) {
                    playlistObj.put("visibility", "private");
                } else {
                    playlistObj.put("visibility", "public");
                }
                playlistObj.put("followers", aux.getFollowers());

                playlistList.add(playlistObj);
            }
            objectNode.put("result", playlistList);
        }
    }
}
