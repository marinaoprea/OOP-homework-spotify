package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import main.CommandInput;
import main.Database;
import main.Notification;
import main.user.Host;
import main.user.User;

import java.util.ArrayList;

public final class AddPodcast extends Command {
    private String message;
    private final String name;
    private final ArrayList<EpisodeInput> episodes;
    public AddPodcast(final CommandInput commandInput) {
        super(commandInput);
        this.name = commandInput.getName();
        this.episodes = commandInput.getEpisodes();
    }

    /**
     * method prints corresponding error message;
     * method checks if user is host, if host has a podcast with the same name and
     * if podcast is valid (does not contain two episodes with the same name)
     * if valid, method adds new podcast in host's podcast list and in database in
     * global list of podcasts
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
        if (host.containsPodcastName(this.name)) {
            this.message = this.getUsername() + " has another podcast with the same name.";
            return;
        }
        if (this.episodes.stream().map(EpisodeInput::getName).distinct().count()
                != episodes.size()) {
            this.message = this.getUsername() + " has the same episode in this podcast.";
            return;
        }
        PodcastInput podcastInput = new PodcastInput();
        podcastInput.setEpisodes(this.episodes);
        podcastInput.setName(this.name);
        podcastInput.setOwner(this.getUsername());
        database.getPodcasts().add(podcastInput);
        host.getPodcasts().add(podcastInput);
        this.message = this.getUsername() + " has added new podcast successfully.";

        main.Notification notification = new Notification("podcast", host);
        host.notify(notification);
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
