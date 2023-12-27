package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.Album;
import main.CommandInput;
import main.Database;
import main.Wrappeable;
import main.user.Artist;
import main.user.User;
import main.wrappers.Wrapper;
import org.checkerframework.checker.units.qual.A;

import java.lang.ref.WeakReference;
import java.util.*;

public class WrapperCommand extends Command{
    private String message;
    private List<Map.Entry<Wrappeable, Integer>> topArtists;
    private List<Map.Entry<String, Integer>> topGenres;
    private List<Map.Entry<Wrappeable, Integer>> topSongs;
    private List<Map.Entry<String, Integer>> topSongsArtist;
    //private List<Map.Entry<Wrappeable, Integer>> topAlbums;
    private List<Map.Entry<String, Integer>> topAlbums;
    private List<Map.Entry<Wrappeable, Integer>> topPodcasts;
    private String type;
    private List<String> topFans;
    private int listeners;

    public WrapperCommand(final CommandInput commandInput) {
        super(commandInput);
    }

    private List<String> extractResultsFans(final HashMap<User, Integer> hashMap) {
        List<Map.Entry<User, Integer>> sorted =
                hashMap.entrySet().stream().sorted(new Comparator<Map.Entry<User, Integer>>() {
                    @Override
                    public int compare(Map.Entry<User, Integer> o1, Map.Entry<User, Integer> o2) {
                        if (o1.getValue() > o2.getValue()) {
                            return -1;
                        }
                        if (o1.getValue() < o2.getValue()) {
                            return 1;
                        }
                        return o1.getKey().getUsername().compareTo(o2.getKey().getUsername());
                    }
                }).limit(Constants.NO_RESULTS_STATISTICS).toList();

        List<String> answer = new ArrayList<>();
        for (Map.Entry<User, Integer> entry : sorted) {
            answer.add(entry.getKey().getUsername());
        }
        return answer;
    }

    private List<Map.Entry<String, Integer>> extractResultsGenre(final HashMap<String, Integer> hashMap) {
        List<Map.Entry<String, Integer>> sorted =
                hashMap.entrySet().stream().sorted(new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        if (o1.getValue() > o2.getValue()) {
                            return -1;
                        }
                        if (o1.getValue() < o2.getValue()) {
                            return 1;
                        }
                        return o1.getKey().compareTo(o2.getKey());
                    }
                }).limit(Constants.NO_RESULTS_STATISTICS).toList();

        return sorted;
    }

    private List<Map.Entry<Wrappeable, Integer>> extractResults(final HashMap<Wrappeable, Integer> hashMap) {
        List<Map.Entry<Wrappeable, Integer>> sorted =
                hashMap.entrySet().stream().sorted(new Comparator<Map.Entry<Wrappeable, Integer>>() {
            @Override
            public int compare(Map.Entry<Wrappeable, Integer> o1, Map.Entry<Wrappeable, Integer> o2) {
                if (o1.getValue() > o2.getValue()) {
                    return -1;
                }
                if (o1.getValue() < o2.getValue()) {
                    return 1;
                }
                return o1.getKey().extractName().compareTo(o2.getKey().extractName());
            }
        }).limit(Constants.NO_RESULTS_STATISTICS).toList();

        return sorted;
    }
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
            topAlbums = this.extractResultsGenre(wrapper.getWrapAlbum());
            topArtists = this.extractResults(wrapper.getWrapArtists());
            topGenres = this.extractResultsGenre(wrapper.getWrapGenre());
            topSongs = this.extractResults(wrapper.getWrapSong());
            topPodcasts = this.extractResults(wrapper.getWrapPodcast());
            return;
        }
        if (database.findArtistByName(this.getUsername())) {
            type = "artist";
            database.simulateAllUsers(this.getTimestamp());

            /*HashMap<Wrappeable, Integer> albums = new HashMap<>();
            HashMap<Wrappeable, Integer> songs = new HashMap<>();
            Artist artist = (Artist) user;
            for (Album album: artist.getAlbums()) {
                albums.put(album, album.getListens());

                for (SongInput song: album.getSongs()) {
                    songs.put(song, song.getListens());
                }
            }*/

            Artist artist = database.findArtist(this.getUsername());

           /* HashMap<String, Integer> songsByName = new HashMap<>();
            Set<Map.Entry<Wrappeable, Integer>> entrySet = artist.getWrapperArtist().getWrapSongs().entrySet();
            for (Map.Entry<Wrappeable, Integer> entry : entrySet) {
                if (songsByName.containsKey(entry.getKey().extractName())) {
                    Integer previousListens = songsByName.remove(entry.getKey().extractName());
                    songsByName.put(entry.getKey().extractName(), previousListens + entry.getValue());
                } else {
                    songsByName.put(entry.getKey().extractName(), entry.getValue());
                }
            }*/

            topAlbums = this.extractResultsGenre(artist.getWrapperArtist().getWrapAlbums());
            topSongs = this.extractResults(artist.getWrapperArtist().getWrapSongs());
            topFans = this.extractResultsFans(artist.getWrapperArtist().getTopFans());
            listeners = artist.getWrapperArtist().getTopFans().size();
        }
    }

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
            if (topAlbums.isEmpty() && topArtists.isEmpty() && topGenres.isEmpty() && topSongs.isEmpty()
                    && topPodcasts.isEmpty()) {
                this.message = "No data to show for user " + this.getUsername() + ".";
                objectNode.put("message", this.message);
                return;
            }

            ObjectNode artist = mapper.createObjectNode();
            for (Map.Entry<Wrappeable, Integer> entry : topArtists) {
                artist.put(entry.getKey().extractName(), entry.getValue());
            }
            resultObject.put("topArtists", artist);

            ObjectNode genres = mapper.createObjectNode();
            for (Map.Entry<String, Integer> entry : topGenres) {
                genres.put(entry.getKey(), entry.getValue());
            }
            resultObject.put("topGenres", genres);

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

            ObjectNode songs = mapper.createObjectNode();
            for (Map.Entry<Wrappeable, Integer> entry : topSongs) {
                songs.put(entry.getKey().extractName(), entry.getValue());
            }
            resultObject.put("topSongs", songs);
        }

        if (type.equals("artist")) {
            if (topFans.isEmpty() && topSongs.isEmpty() && topAlbums.isEmpty()) {
                this.message = "No data to show for artist " + this.getUsername() + ".";
                objectNode.put("message", this.message);
                return;
            }

            /*ObjectNode songs = mapper.createObjectNode();
            for (Map.Entry<String, Integer> entry : topSongsArtist) {
                songs.put(entry.getKey(), entry.getValue());
            }
            resultObject.put("topSongs", songs);*/

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

            ArrayNode fans = mapper.createArrayNode();
            for (String fan: this.topFans) {
                fans.add(fan);
            }
            resultObject.put("topFans", fans);

            resultObject.put("listeners", listeners);
        }
        objectNode.put("result", resultObject);
    }
}
