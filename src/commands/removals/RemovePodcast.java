package commands.removals;

import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import fileio.input.PodcastInput;
import main.CommandInput;
import main.Database;
import main.user.Host;
import main.user.User;

public final class RemovePodcast extends Command {
    private String message;
    private final String name;
    public RemovePodcast(final CommandInput commandInput) {
        super(commandInput);
        this.name = commandInput.getName();
    }

    private boolean checkPodcast(final String podcastName, final Database database) {
        for (User user: database.getUsers()) {
            if (user.isPlaying() && user.getSelectedType().equals("podcast")) {
                user.simulate(this.getTimestamp(), database);
                if (user.getLoadedSourceName().equals(podcastName)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * method sets corresponding error messages;
     * method checks if user exists, if user's a host, if user has a podcast
     * with given name;
     * if valid, method checks if podcast can be deleted; if so, method calls podcast
     * clearance method and then removes podcast from artist's list and from global
     * list of podcasts in database
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (!database.findHostByName(this.getUsername())) {
            this.message = this.getUsername() + " is not a host.";
            return;
        }
        Host host = (Host) user;
        if (!host.containsPodcastName(this.name)) {
            this.message = this.getUsername() + " doesn't have a podcast with the given name.";
            return;
        }
        if (!checkPodcast(this.name, database)) {
            this.message = this.getUsername() + " can't delete this podcast.";
            return;
        }
        this.message = this.getUsername() + " deleted the podcast successfully.";
        PodcastInput podcast = host.getPodcastByName(this.name);
        host.getPodcasts().remove(podcast);
        database.getPodcasts().remove(podcast);
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
