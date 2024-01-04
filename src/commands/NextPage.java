package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;

public final class NextPage extends Command {
    private String message;
    public NextPage(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sets corresponding error message;
     * method calls for navigation redo
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }

        this.message = user.getNavigation().redo(user);
    }

    /**
     * @param objectNode created ObjectNode
     */
    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
