package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;

public final class AdBreak extends Command {
    private String message;
    private final int price;
    public AdBreak(final CommandInput commandInput) {
        super(commandInput);
        this.price = commandInput.getPrice();
    }

    /**
     * method sets corresponding error messages;
     * method sets user flag for ad playing; method sets user's price field for
     * later ad monetization (when ad is simulated);
     * simulation for user is performed up until this moment
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
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

    /**
     * @param objectNode created ObjectNode
     */
    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
