package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.PodcastInput;
import main.CommandInput;
import main.Database;
import main.user.User;

public final class Forward extends Command {
    private String message;
    public Forward(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sets corresponding error messages;
     * method calls for simulation up until this moment;
     * method sets episode cursor in a podcast 90 seconds to the right if possible;
     * if remaining time of episode is less than 90 seconds, next episode is set to
     * be playing, regarding repeat status;
     * if episode is set to repeat once, it is played once more and repeat status is changed
     * to "No Repeat";
     * if episode is set to repeat infinitely, it is played once more and repeat status remains
     * unchanged;
     * if next episode is nonexistent, user is set on pause
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        user.simulate(this.getTimestamp(), database);
        if (!user.isLoaded()) {
            this.message = "Please load a source before attempting to forward.";
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
        String podcastName = user.getLastSearch().getResults().get(user.getSelectedIndex() - 1);
        PodcastInput podcast = database.findPodcast(podcastName);
        if (podcast == null) {
            return;
        }
        int newTimeRelative = user.getTimeRelativeToSong() + Constants.NO_SECONDS_FWD_BACK;
        if (newTimeRelative
                < podcast.getEpisodes().get(user.getSelectedIndexInList() - 1).getDuration()) {
            user.setTimeRelativeToSong(newTimeRelative);
            user.setTimeLoaded(this.getTimestamp());
            this.message = "Skipped forward successfully.";
            return;
        }

        if (user.getRepeat() == 1) { // repeat once
            user.setTimeLoaded(this.getTimestamp());
            user.setTimeRelativeToSong(0);
            user.setRepeat(0);
            this.message = "Skipped forward successfully.";
            return;
        }
        if (user.getRepeat() == 2) {
            user.setTimeLoaded(this.getTimestamp());
            user.setTimeRelativeToSong(0);
            this.message = "Skipped forward successfully";
            return;
        }
        // no repeat
        int newIndex = user.getSelectedIndexInList() + 1;
        if (newIndex <= podcast.getEpisodes().size()) {
            user.setSelectedIndexInList(newIndex);
            user.setTimeRelativeToSong(0);
            user.setTimeLoaded(this.getTimestamp());
            this.message = "Skipped forward successfully";
            return;
        }
        // ended podcast
        user.setLoaded(false);
        user.setTimeLoaded(0);
        user.setSelectedIndex(0);
        user.setPlaying(false);
        user.setSelectedIndexInList(1);
        this.message = "Skipped forward successfully";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
