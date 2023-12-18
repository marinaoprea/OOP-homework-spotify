package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;

public final class SwitchConnectionStatus extends Command {
    private String message;

    public SwitchConnectionStatus(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sets corresponding error messages;
     * method checks if user exists, if user's a normal user;
     * if valid, method sets on/offline status opposite to previous state;
     * if user was online and playing, simulation up until this moment is performed;
     * when he goes back online last update time is set to this timestamp (in order to bypass
     * his offline time being taken into consideration)
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (!database.getNormalUsers().contains(user)) {
            this.message = this.getUsername() + " is not a normal user.";
            return;
        }
        this.message = this.getUsername() + " has changed status successfully.";
        if (user.isConnectionStatus() && user.isPlaying()) {
            user.simulate(this.getTimestamp(), database);
        } else {
            if (user.getTimeLoaded() != 0) {
                user.setTimeLoaded(this.getTimestamp());
            }
        }
        user.setConnectionStatus(!user.isConnectionStatus());
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
