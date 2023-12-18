package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;

import java.util.ArrayList;

public final class GetAllUsers extends Command {
    private final ArrayList<User> allUsers = new ArrayList<User>();
    public GetAllUsers(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method adds all users to list following specific order: normal users,
     * artists and hosts
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        allUsers.addAll(database.getNormalUsers());
        allUsers.addAll(database.getArtists());
        allUsers.addAll(database.getHosts());
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        objectNode.put("timestamp", this.getTimestamp());
        objectNode.put("command", this.getCommand());
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode resultsArray = mapper.createArrayNode();
        for (User user : allUsers) {
            resultsArray.add(user.getUsername());
        }
        objectNode.put("result", resultsArray);
    }
}
