package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.Monetization;
import main.user.User;

public final class CancelPremium extends Command {
    private String message;

    public CancelPremium(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sets corresponding error messages;
     * method pops saved song history before premium subscription; method clears saved song
     * history;
     * method resets premium flag for user and calls for monetization calculation for the
     * ended premium subscription;
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

        if (!user.isPremium()) {
            this.message = this.getUsername() + " is not a premium user.";
            return;
        }

        user.simulate(this.getTimestamp(), database);
        Monetization.calculateMonetization(user, database, Constants.PREMIUM_CREDIT);
        user.setPremium(false);
        user.getSongHistory().getSongMap().clear();

        user.getSongHistory().copy(user.getCopyHistory().getSongMap());
        user.getCopyHistory().getSongMap().clear();
        this.message = this.getUsername() + " cancelled the subscription successfully.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", message);
    }
}
