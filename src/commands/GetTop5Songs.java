package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;

public final class GetTop5Songs extends Command {
    private String[] result = new String[Constants.NO_RESULTS_STATISTICS];
    private Integer[] indexes = new Integer[Constants.NO_RESULTS_STATISTICS];
    public GetTop5Songs(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method selects top 5 songs based on the number of likes array;
     * insertion sort approach is used, but only 5 results are memorized;
     * thus time complexity is O(n), n number of songs
     * @param database input library
     */
    @Override
    public void execute(final Database database) {
        for (int i = 0; i < Constants.NO_RESULTS_STATISTICS; i++) {
            indexes[i] = i;
        }
        for (int i = 0; i < Constants.NO_RESULTS_STATISTICS; i++) {
            for (int j = i + 1; j < Constants.NO_RESULTS_STATISTICS; j++) {
                if (database.getNoLikesPerSong().get(indexes[j])
                        > database.getNoLikesPerSong().get(indexes[i])) {
                    int aux = indexes[i];
                    indexes[i] = indexes[j];
                    indexes[j] = aux;
                }
            }
        }

        for (int i = Constants.NO_RESULTS_STATISTICS; i < database.getSongs().size(); i++) {
            int j = Constants.NO_RESULTS_STATISTICS - 1;
            while (j >= 0 && database.getNoLikesPerSong().get(i)
                    > database.getNoLikesPerSong().get(indexes[j])) {
                j--;
            }
            j++;
            if (j == Constants.NO_RESULTS_STATISTICS) {
                continue;
            }
            for (int k = Constants.NO_RESULTS_STATISTICS - 1; k >= j + 1; k--) {
                indexes[k] = indexes[k - 1];
            }
            indexes[j] = i;
        }

        for (int i = 0; i < Constants.NO_RESULTS_STATISTICS; i++) {
            result[i] = database.getSongs().get(indexes[i]).getName();
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
