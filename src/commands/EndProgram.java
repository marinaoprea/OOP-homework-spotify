package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.user.Artist;
import main.user.Monetization;
import main.user.User;

import java.util.Comparator;
import java.util.List;

public class EndProgram extends Command {

    List<Artist> resultArtist;

    public EndProgram(CommandInput commandInput) {
        super(commandInput);
    }

    @Override
    public void execute(Database database) {
        for (User user : database.getUsers()) {
            user.simulate(this.getTimestamp(), database);
            Monetization.calculateMonetization(user, database);
        }
        List<Artist> sorted =
                database.getArtists().stream().filter(artist -> artist.getRevenue().isWasListened()).
                        sorted(new Comparator<Artist>() {
            @Override
            public int compare(Artist o1, Artist o2) {
                if (o1.getRevenue().getTotalRevenue() > o2.getRevenue().getTotalRevenue()) {
                    return -1;
                }
                if (o1.getRevenue().getTotalRevenue() < o2.getRevenue().getTotalRevenue()) {
                    return 1;
                }
                return o1.getUsername().compareTo(o2.getUsername());
            }
        }).toList();
        int ranking = 0;
        for (Artist artist: sorted) {
            ranking++;
            artist.getRevenue().setRanking(ranking);
        }
        resultArtist = sorted;
    }

    @Override
    public void convertToObjectNode(ObjectNode objectNode) {
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
