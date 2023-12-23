package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;

public class NextPage extends Command {
    private String message;
    public NextPage(CommandInput commandInput) {
        super(commandInput);
    }

    @Override
    public void execute(Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }

        this.message = user.getNavigation().redo(database, user);
    }

    @Override
    public void convertToObjectNode(ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
