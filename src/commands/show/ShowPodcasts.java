package commands.show;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import main.CommandInput;
import main.Database;
import main.user.Host;

import java.util.ArrayList;

public final class ShowPodcasts extends Command {
    private ArrayList<PodcastInput> podcasts;
    public ShowPodcasts(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method gets a host's podcasts collection and sets it as command result
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        Host host = database.findHost(this.getUsername());
        this.podcasts = host.getPodcasts();
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode resultPodcasts = mapper.createArrayNode();
        for (PodcastInput podcast: podcasts) {
            ObjectNode objectNodePodcast = mapper.createObjectNode();
            objectNodePodcast.put("name", podcast.getName());

            ArrayNode podcastEpisodes = mapper.createArrayNode();
            for (EpisodeInput episode: podcast.getEpisodes()) {
                podcastEpisodes.add(episode.getName());
            }
            objectNodePodcast.put("episodes", podcastEpisodes);
            resultPodcasts.add(objectNodePodcast);
        }
        objectNode.put("result", resultPodcasts);
    }
}
