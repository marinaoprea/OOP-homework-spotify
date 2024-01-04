package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.Wrappeable;
import main.user.Artist;
import main.user.Host;
import main.user.User;
import main.wrappers.Wrapper;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public final class WrapperCommand extends Command {
    private String message;
    private List<Map.Entry<String, Integer>> topArtists;
    private List<Map.Entry<String, Integer>> topGenres;
    private List<Map.Entry<Wrappeable, Integer>> topSongs;
    private List<Map.Entry<String, Integer>> topAlbums;
    private List<Map.Entry<Wrappeable, Integer>> topPodcasts;
    private String type;
    private List<String> topFans;
    private int listeners;

    public WrapperCommand(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method extracts top fans depending on number of listens from given hashmap
     * @param hashMap hashmap of fans to number of listens
     * @return list of fans' usernames limited to 5 results
     */
    private List<String> extractResultsFans(final HashMap<User, Integer> hashMap) {
        List<Map.Entry<User, Integer>> sorted =
                hashMap.entrySet().stream().sorted((o1, o2) -> {
                    if (o1.getValue() > o2.getValue()) {
                        return -1;
                    }
                    if (o1.getValue() < o2.getValue()) {
                        return 1;
                    }
                    return o1.getKey().getUsername().compareTo(o2.getKey().getUsername());
                }).limit(Constants.NO_RESULTS_STATISTICS).toList();

        List<String> answer = new ArrayList<>();
        for (Map.Entry<User, Integer> entry : sorted) {
            answer.add(entry.getKey().getUsername());
        }
        return answer;
    }

    /**
     * method extracts results from hashmap
     * @param hashMap hashmap of string to integer
     * @return list of top results based on integer, limited to 5 results
     */
    private List<Map.Entry<String, Integer>> extractResultsString(final HashMap<String, Integer> hashMap) {
        List<Map.Entry<String, Integer>> sorted =
                hashMap.entrySet().stream().sorted((o1, o2) -> {
                    if (o1.getValue() > o2.getValue()) {
                        return -1;
                    }
                    if (o1.getValue() < o2.getValue()) {
                        return 1;
                    }
                    return o1.getKey().compareTo(o2.getKey());
                }).limit(Constants.NO_RESULTS_STATISTICS).toList();

        return sorted;
    }

    /**
     * method extracts top results depending on integer value from hashmap of
     * wrappable object to number of listens
     * @param hashMap input hashmap of wrappable object to number of listens
     * @return list of top wrappable objects, limited to 5 results
     */
    private List<Map.Entry<Wrappeable, Integer>> extractResults(final HashMap<Wrappeable, Integer> hashMap) {
        List<Map.Entry<Wrappeable, Integer>> sorted =
                hashMap.entrySet().stream().sorted((o1, o2) -> {
                    if (o1.getValue() > o2.getValue()) {
                        return -1;
                    }
                    if (o1.getValue() < o2.getValue()) {
                        return 1;
                    }
                    return o1.getKey().extractName().compareTo(o2.getKey().extractName());
                }).limit(Constants.NO_RESULTS_STATISTICS).toList();

        return sorted;
    }

    /**
     * method sets corresponding error message;
     * method updates results lists depending on user's type
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "No data to show for " + this.getUsername() + ".";
            return;
        }
        if (database.getNormalUsers().contains(user)) { //normal user wrapper
            type = "user";
            user.simulate(this.getTimestamp(), database);
            Wrapper wrapper = user.getWrapper();
            topAlbums = this.extractResultsString(wrapper.getWrapAlbum());
            topArtists = this.extractResultsString(wrapper.getWrapArtists());
            topGenres = this.extractResultsString(wrapper.getWrapGenre());
            topSongs = this.extractResults(wrapper.getWrapSong());
            topPodcasts = this.extractResults(wrapper.getWrapPodcast());
            return;
        }
        if (database.findArtistByName(this.getUsername())) {
            type = "artist";
            database.simulateAllUsers(this.getTimestamp());

            Artist artist = database.findArtist(this.getUsername());

            topAlbums = this.extractResultsString(artist.getWrapperArtist().getWrapAlbums());
            topSongs = this.extractResults(artist.getWrapperArtist().getWrapSongs());
            topFans = this.extractResultsFans(artist.getWrapperArtist().getTopFans());
            listeners = artist.getWrapperArtist().getTopFans().size();
        }

        if (database.findHostByName(this.getUsername())) {
            type = "host";
            database.simulateAllUsers(this.getTimestamp());

            Host host = database.findHost(this.getUsername());
            topPodcasts = this.extractResults(host.getWrapperHost().getWrapPodcasts());
            listeners = host.getWrapperHost().getTopFans().size();
        }
    }

    /**
     * @param objectNode created ObjectNode
     */
    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultObject = mapper.createObjectNode();

        if (this.message != null || this.type == null) {
            objectNode.put("message", this.message);
            return;
        }

        if (type.equals("user")) {
            if (topAlbums.isEmpty() && topArtists.isEmpty()
                    && topGenres.isEmpty() && topSongs.isEmpty() && topPodcasts.isEmpty()) {
                this.message = "No data to show for user " + this.getUsername() + ".";
                objectNode.put("message", this.message);
                return;
            }

            ObjectNode artist = mapper.createObjectNode();
            for (Map.Entry<String, Integer> entry : topArtists) {
                artist.put(entry.getKey(), entry.getValue());
            }
            resultObject.put("topArtists", artist);

            ObjectNode genres = mapper.createObjectNode();
            for (Map.Entry<String, Integer> entry : topGenres) {
                genres.put(entry.getKey(), entry.getValue());
            }
            resultObject.put("topGenres", genres);

            ObjectNode songs = mapper.createObjectNode();
            for (Map.Entry<Wrappeable, Integer> entry : topSongs) {
                songs.put(entry.getKey().extractName(), entry.getValue());
            }
            resultObject.put("topSongs", songs);

            ObjectNode albums = mapper.createObjectNode();
            for (Map.Entry<String, Integer> entry : topAlbums) {
                albums.put(entry.getKey(), entry.getValue());
            }
            resultObject.put("topAlbums", albums);

            ObjectNode podcasts = mapper.createObjectNode();
            for (Map.Entry<Wrappeable, Integer> entry : topPodcasts) {
                podcasts.put(entry.getKey().extractName(), entry.getValue());
            }
            resultObject.put("topEpisodes", podcasts);
        }

        if (type.equals("artist")) {
            if (topFans.isEmpty() && topSongs.isEmpty() && topAlbums.isEmpty()) {
                this.message = "No data to show for artist " + this.getUsername() + ".";
                objectNode.put("message", this.message);
                return;
            }

            ObjectNode albums = mapper.createObjectNode();
            for (Map.Entry<String, Integer> entry : topAlbums) {
                albums.put(entry.getKey(), entry.getValue());
            }
            resultObject.put("topAlbums", albums);

            ObjectNode songs = mapper.createObjectNode();
            for (Map.Entry<Wrappeable, Integer> entry : topSongs) {
                songs.put(entry.getKey().extractName(), entry.getValue());
            }
            resultObject.put("topSongs", songs);

            ArrayNode fans = mapper.createArrayNode();
            for (String fan: this.topFans) {
                fans.add(fan);
            }
            resultObject.put("topFans", fans);

            resultObject.put("listeners", listeners);
        }

        if (type.equals("host")) {
            if (topPodcasts.isEmpty()) {
                this.message = "No data to show for host " + this.getUsername() + ".";
                objectNode.put("message", this.message);
                return;
            }

            ObjectNode podcasts = mapper.createObjectNode();
            for (Map.Entry<Wrappeable, Integer> entry : topPodcasts) {
                podcasts.put(entry.getKey().extractName(), entry.getValue());
            }
            resultObject.put("topEpisodes", podcasts);

            resultObject.put("listeners", listeners);
        }
        objectNode.put("result", resultObject);
    }
}
