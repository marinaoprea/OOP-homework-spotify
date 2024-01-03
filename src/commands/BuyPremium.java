package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;

public final class BuyPremium extends Command {
    private String message;

    public BuyPremium(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sets corresponding error messages;
     * method saves song history in order for ad to be later monetized;
     * method resets song history; method sets flag for user being premium and resets flag for ad
     * as premium users can skip ads;
     * user simulation is performed up until this point in order for statistics to be updated
     * @param database extended input library
     */
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
        // history before premium
        user.getCopyHistory().copy(user.getSongHistory().getSongMap());

        user.getSongHistory().getSongMap().clear();
        user.setPremium(true);
        user.setPlayAd(false);

        this.message = this.getUsername() + " bought the subscription successfully.";
    }

    /**
     * @param objectNode created ObjectNode
     */
    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", message);
    }
}
