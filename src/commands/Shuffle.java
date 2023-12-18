package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;

public final class Shuffle extends Command {
    private final int seed;
    private String message;
    public Shuffle(final CommandInput commandInput) {
        super(commandInput);
        this.seed = commandInput.getSeed();
    }

    /**
     * method sets corresponding error messages;
     * if playlist was already shuffled, it is set to be un-shuffled;
     * otherwise, an array of shuffled indexes is set taking into consideration
     * the seed given
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (!user.isLoaded()) {
            this.message = "Please load a source before using the shuffle function.";
            return;
        }
        if (!user.getSelectedType().equals("playlist") && !user.getSelectedType().equals("album")) {
            this.message = "The loaded source is not a playlist or an album.";
            return;
        }
        user.simulate(this.getTimestamp(), database);
        if (!user.isLoaded()) {
            this.message = "Please load a source before using the shuffle function.";
            return;
        }

        if (user.getShuffle()) {
            user.setShuffle(false);
            this.message = "Shuffle function deactivated successfully.";
            return;
        }

        user.setShuffle(true);
        user.setShuffleSeed(this.seed);
        this.message = "Shuffle function activated successfully.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
