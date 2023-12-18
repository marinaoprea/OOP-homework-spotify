package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.user.User;

import java.util.ArrayList;

public final class ShowPreferredSongs extends Command {
    private ArrayList<SongInput> songs;
    public ShowPreferredSongs(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method gets user's favourite songs collection and sets it as command result
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        this.songs = user.getFavourites().getSongs();
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode results = mapper.createArrayNode();
        if (!this.songs.isEmpty()) {
            for (SongInput aux : this.songs) {
                results.add(aux.getName());
            }
        }
        objectNode.set("result", results);
    }
}
