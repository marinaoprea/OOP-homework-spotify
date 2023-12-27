package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.Monetization;
import main.user.User;

public class CancelPremium extends Command {
    private String message;

    public CancelPremium(CommandInput commandInput) {
        super(commandInput);
    }

    @Override
    public void execute(Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }

        if (!user.isPremium()) {
            this.message = this.getUsername() + " is not a premium user.";
            return;
        }

        user.simulate(this.getTimestamp(), database);
        Monetization.calculateMonetization(user, database);
        user.setPremium(false);
        user.getSongHistory().getSongMap().clear();
        this.message = this.getUsername() + " cancelled the subscription successfully.";
    }

    @Override
    public void convertToObjectNode(ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", message);
    }
}
