package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.Monetization;
import main.user.User;

public class AdBreak extends Command {
    private String message;
    private int price;
    public AdBreak(CommandInput commandInput) {
        super(commandInput);
        this.price = commandInput.getPrice();
    }

    @Override
    public void execute(Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (!user.isConnectionStatus()) {
            this.message = this.getUsername() + " is offline.";
            return;
        }

        user.simulate(this.getTimestamp(), database);
        if (!user.isPlaying() || user.getSelectedType().equals("podcast")) {
            this.message = this.getUsername() + " is not playing any music.";
            return;
        }

        user.setPlayAd(true);
        user.setAdPrice(this.price);
        this.message = "Ad inserted successfully.";
    }

    @Override
    public void convertToObjectNode(ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
