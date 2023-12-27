package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.user.User;

public class BuyPremium extends Command {
    private String message;

    public BuyPremium(final CommandInput commandInput) {
        super(commandInput);
    }

    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (user.isPremium()) {
            this.message = this.getUsername() + " is already a premium user.";
            return;
        }

        user.simulate(this.getTimestamp(), database);
        user.getSongHistory().getSongMap().clear();
        user.setPremium(true);

        this.message = this.getUsername() + " bought the subscription successfully.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", message);
    }
}
