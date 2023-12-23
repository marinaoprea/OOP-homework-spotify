package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.Artist;
import main.user.User;

import java.util.ArrayList;

public class SeeMerch extends Command {
    private ArrayList<Artist.Merch> resultMerch;
    private String message;

    public SeeMerch(CommandInput commandInput) {
        super(commandInput);
    }
    @Override
    public void execute(Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }

        resultMerch = user.getBoughtMerch();
    }

    @Override
    public void convertToObjectNode(ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);

        if (this.message != null) {
            objectNode.put("message", this.message);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode resultArray = mapper.createArrayNode();
        for (Artist.Merch merch : resultMerch) {
            resultArray.add(merch.getName());
        }
        objectNode.put("result", resultArray);
    }
}
