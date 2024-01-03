package commands.statistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import commands.Constants;
import main.user.Artist;
import main.CommandInput;
import main.Database;

import java.util.List;

public final class GetTop5Artists extends Command {
    private List<Artist> topArtists;
    public GetTop5Artists(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sorts artists based on total number of likes obtained;
     * method limits results to top 5
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        topArtists = database.getArtists().stream().sorted(
                (o1, o2) -> o2.getNoLikes() - o1.getNoLikes()).
                limit(Constants.NO_RESULTS_STATISTICS).toList();
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        objectNode.put("timestamp", this.getTimestamp());
        objectNode.put("command", this.getCommand());
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode artistArray = mapper.createArrayNode();
        for (Artist artist: topArtists) {
            artistArray.add(artist.getUsername());
        }
        objectNode.put("result", artistArray);
    }
}
