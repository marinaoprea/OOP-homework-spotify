package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;

public final class PlayPause extends Command {
    private String message;
    public PlayPause(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sets corresponding error messages;
     * if loaded source was playing, simulation is performed up until this point and
     * user is set on pause;
     * otherwise, user is set on playing and the moment the audio file was loaded
     * is set to this command's timestamp
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            return;
        }
        user.simulate(this.getTimestamp(), database);
        if (user.getTimeLoaded() == 0) {
            this.message = "Please load a source before attempting to pause or resume playback.";
            return;
        }
        if (user.isPlaying()) {
            user.simulate(this.getTimestamp(), database);
            user.setPlaying(false);
            this.message = "Playback paused successfully.";
            return;
        }
        if (user.isLoaded()) {
            user.setPlaying(true);
            user.setTimeLoaded(this.getTimestamp());
            this.message = "Playback resumed successfully.";
            return;
        }
        this.message = "Please load a source before attempting to pause or resume playback.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
