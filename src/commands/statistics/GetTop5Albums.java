package commands.statistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import commands.Constants;
import main.Album;
import main.CommandInput;
import main.Database;

import java.util.List;

public final class GetTop5Albums extends Command {
    private List<Album> topAlbums;
    public GetTop5Albums(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sorts albums according to total number of likes and lexicographic if equal;
     * method limits results to 5 albums
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        topAlbums = database.getAlbums().stream().sorted(
                (o1, o2) ->
                        o1.getNoLikes() != o2.getNoLikes()
                                ? o2.getNoLikes() - o1.getNoLikes()
                                : o1.getName().compareTo(o2.getName()))
                .limit(Constants.NO_RESULTS_STATISTICS).toList();
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        objectNode.put("timestamp", this.getTimestamp());
        objectNode.put("command", this.getCommand());
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode albumArray = mapper.createArrayNode();
        for (Album album: topAlbums) {
            albumArray.add(album.getName());
        }
        objectNode.put("result", albumArray);
    }
}
