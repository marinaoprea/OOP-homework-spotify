package commands.statistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import main.CommandInput;
import main.Database;
import main.user.User;

import java.util.ArrayList;

public final class GetOnlineUsers extends Command {
    private final ArrayList<String> onlineUsers = new ArrayList<String>();
    public GetOnlineUsers(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method adds to list all normal users that are online
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        for (User user : database.getNormalUsers()) {
            if (user.isConnectionStatus()) {
                onlineUsers.add(user.getUsername());
            }
        }
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        objectNode.put("timestamp", this.getTimestamp());
        objectNode.put("command", this.getCommand());
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode online = mapper.createArrayNode();
        if (!this.onlineUsers.isEmpty()) {
            for (String username : this.onlineUsers) {
                online.add(username);
            }
        }
        objectNode.put("result", online);
    }
}
