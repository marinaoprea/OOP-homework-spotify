package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.Playlist;

import java.util.Comparator;
import java.util.List;

public final class GetTop5Playlists extends Command {
    private final String[] result = new String[Constants.NO_RESULTS_STATISTICS];
    public GetTop5Playlists(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method filters playlists by privacy setting; method takes into consideration public
     * playlists and sorts them after followers number; method saves into result array top
     * 5 playlists' names;
     * time complexity O(n * log n), n number of playlists
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        List<Playlist> resultPlaylist =
                database.getGlobalPlaylists().stream().filter(playlist
                        -> !playlist.isVisibility()).sorted(new Comparator<Playlist>() {
                    @Override
                    public int compare(final Playlist o1, final Playlist o2) {
                        if (o1.getFollowers() > o2.getFollowers()) {
                            return -1;
                        }
                        if (o1.getFollowers() == o2.getFollowers()
                                && o1.getTimestamp() < o2.getTimestamp()) {
                            return -1;
                        }
                        return +1;
                    }
                }).toList();
        for (int i = 0; i < Constants.NO_RESULTS_STATISTICS && i < resultPlaylist.size(); i++) {
            result[i] = resultPlaylist.get(i).getName();
        }
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        objectNode.put("command", this.getCommand());
        objectNode.put("timestamp", this.getTimestamp());
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode results = mapper.createArrayNode();
        for (int i = 0; i < Constants.NO_RESULTS_STATISTICS; i++) {
            if (result[i] != null) {
                results.add(result[i]);
            }
        }
        objectNode.set("result", results);
    }
}

