package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;

public final class Backward extends Command {
    private String message;
    public Backward(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sets corresponding error messages;
     * method calls for simulation up until this moment
     * method sets cursor in episode 90 seconds to the left if possible;
     * if episode was running for less than 90 seconds, episode cursor is
     * set to the start of the episode
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        user.simulate(this.getTimestamp(), database);
        if (!user.isLoaded()) {
            this.message = "Please load a source before rewinding.";
            return;
        }
        if (!user.getSelectedType().equals("podcast")) {
            this.message = "The loaded source is not a podcast.";
            return;
        }
        if (user.getSelectedIndex() < 1
                || user.getSelectedIndex() > user.getLastSearch().getResults().size()) {
            return;
        }
        int newTimeRelative = user.getTimeRelativeToSong() - Constants.NO_SECONDS_FWD_BACK;
        if (newTimeRelative > 0) {
            user.setTimeRelativeToSong(newTimeRelative);
            user.setTimeLoaded(this.getTimestamp());
            this.message = "Rewound successfully.";
            return;
        }

        user.setTimeRelativeToSong(0);
        user.setTimeLoaded(this.getTimestamp());
        this.message = "Rewound successfully";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
