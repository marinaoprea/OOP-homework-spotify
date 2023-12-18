package commands;


import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;

public final class Repeat extends Command {
    private String message;
    public Repeat(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sets corresponding error messages;
     * method changes repeat mode of user circularly;
     * repeat status has circularly the following values: 0, 1, 2, 0, 1, 2, ...
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        user.simulate(this.getTimestamp(), database);
        if (user == null) {
            this.message = "User does not exist.";
            return;
        }
        if (!user.isLoaded()) {
            this.message = "Please load a source before setting the repeat status.";
            return;
        }
        user.simulate(this.getTimestamp(), database);
        user.setRepeat((user.getRepeat() + 1) % Constants.NO_REPEAT_MODES);
        if (user.getRepeat() == 0) {
            this.message = "Repeat mode changed to no repeat.";
            return;
        }
        if (user.getRepeat() == 1) {
            if (user.getSelectedType().equals("playlist")) {
                this.message = "Repeat mode changed to repeat all.";
            } else {
                this.message = "Repeat mode changed to repeat once.";
            }
        } else {
            if (user.getSelectedType().equals("playlist")) {
                this.message = "Repeat mode changed to repeat current song.";
            } else {
                this.message = "Repeat mode changed to repeat infinite.";
            }
        }
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}

