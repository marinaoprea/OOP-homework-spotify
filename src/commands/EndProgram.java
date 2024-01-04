package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.user.Artist;
import main.user.Monetization;
import main.user.User;
import java.util.List;

public final class EndProgram extends Command {

    private List<Artist> resultArtist;

    public EndProgram(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method simulates all users up until moment of program ending and calls for
     * monetization updates;
     * method truncates total revenue to 2 decimals
     * method sorts artists depending on total revenue obtained; if equal, lexicographic sort
     * is performed
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        for (User user : database.getUsers()) {
            user.simulate(this.getTimestamp(), database);
            if (user.isPremium()) {
                Monetization.calculateMonetization(user, database, Constants.PREMIUM_CREDIT);
            }
        }
        for (Artist artist: database.getArtists()) {
            Double songRevenue = Math.round(artist.getRevenue().getSongRevenue() * 100) / 100.0;
            artist.getRevenue().setSongRevenue(songRevenue);
        }
        List<Artist> sorted =
                database.getArtists().stream()
                        .filter(artist -> artist.getRevenue().isWasListened()).
                        sorted((o1, o2) -> {
                            if (o1.getRevenue().getTotalRevenue()
                                    > o2.getRevenue().getTotalRevenue()) {
                                return -1;
                            }
                            if (o1.getRevenue().getTotalRevenue()
                                    < o2.getRevenue().getTotalRevenue()) {
                                return 1;
                            }
                            return o1.getUsername().compareTo(o2.getUsername());
                        }).toList();
        int ranking = 0;
        for (Artist artist: sorted) {
            ranking++;
            artist.getRevenue().setRanking(ranking);
        }
        resultArtist = sorted;
    }

    /**
     * @param objectNode created ObjectNode
     */
    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        objectNode.put("command", this.getCommand());
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode resultObject = mapper.createObjectNode();
        for (Artist artist: resultArtist) {
            ObjectNode artistObject = mapper.createObjectNode();
            artistObject.put("songRevenue", artist.getRevenue().getSongRevenue());
            artistObject.put("merchRevenue", artist.getRevenue().getMerchRevenue());
            artistObject.put("ranking", artist.getRevenue().getRanking());
            SongInput mostProfitableSong = artist.getRevenue().getMostProfitableSong();
            if (mostProfitableSong == null) {
                artistObject.put("mostProfitableSong", "N/A");
            } else {
                artistObject.put("mostProfitableSong", mostProfitableSong.getName());
            }

            resultObject.put(artist.getUsername(), artistObject);
        }

        objectNode.put("result", resultObject);
    }
}
