package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.Playlist;
import main.user.Artist;
import main.user.User;

import java.util.*;

public class UpdateRecommendations extends Command {
    private String recommendationType;
    private String message;
    public UpdateRecommendations(CommandInput commandInput) {
        super(commandInput);
        this.recommendationType = commandInput.getRecommendationType();
    }

    private List<SongInput> getRecommendationByGenre(final Database database, final int limit, final String genre) {
        return database.getSongs().stream().filter(songInput -> songInput.getGenre().equals(genre)).sorted(new Comparator<SongInput>() {
            @Override
            public int compare(SongInput o1, SongInput o2) {
                if (o1.getNoLikes() == o2.getNoLikes()) {
                    return o1.getName().compareTo(o2.getName());
                }
                return o2.getNoLikes() - o1.getNoLikes();
            }
        }).limit(limit).toList();
    }

    @Override
    public void execute(Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (!database.getNormalUsers().contains(user)) {
            this.message = this.getUsername() + " is not a normal user.";
            return;
        }
        if (this.recommendationType.equals("fans_playlist")) {
            user.simulate(this.getTimestamp(), database);
            Artist artist = user.listenedArtist(database);
            if (artist == null) {
                this.message = "No new recommendations were found";
                return;
            }

            List <Map.Entry<User, Integer>> topFans = artist.getWrapperArtist().getTopFans().entrySet().stream()
                    .sorted(new Comparator<Map.Entry<User, Integer>>() {
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

            List <SongInput> songs = new ArrayList<>();
            for (Map.Entry<User, Integer> entry: topFans) {
                User user1 = entry.getKey();
                songs.addAll(user1.getFavourites().getSongs().stream().sorted(new Comparator<SongInput>() {
                    @Override
                    public int compare(SongInput o1, SongInput o2) {
                        if (o1.getNoLikes() == o2.getNoLikes()) {
                            return o1.getName().compareTo(o2.getName());
                        }
                        return o2.getNoLikes() - o1.getNoLikes();
                    }
                }).toList());
            }
            List <SongInput> sortedSongs = songs.stream().sorted(new Comparator<SongInput>() {
                @Override
                public int compare(SongInput o1, SongInput o2) {
                    if (o1.getNoLikes() == o2.getNoLikes()) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    return o2.getNoLikes() - o1.getNoLikes();
                }
            }).toList();
            Playlist newPlaylist = new Playlist();
            newPlaylist.getSongs().addAll(new HashSet<>(sortedSongs));
            newPlaylist.setName(artist.getUsername() + " Fan Club recommendations");

            user.getHomePage().getRecommendations().add(newPlaylist);

            this.message = "The recommendations for user " + this.getUsername() + " have been updated successfully.";
            return;
        }

        if (this.recommendationType.equals("random_song")) {
            user.simulate(this.getTimestamp(), database);
            if (!user.getSelectedType().equals("song")) {
                this.message = "No new recommendations were found";
                return;
            }

            SongInput songInput = database.findSong(user.getLoadedSourceName(), user.getLoadedSourceId());
            if (songInput == null || user.getTimeRelativeToSong() < 30) {
                this.message = "No new recommendations were found";
                return;
            }

            List <SongInput> sameGenre = database.getSongs().stream()
                            .filter(songInput1 -> songInput1.getGenre().equals(songInput.getGenre())).toList();
            Random generator = new Random(user.getTimeRelativeToSong());
            int index = generator.nextInt(sameGenre.size());
            SongInput recommended = sameGenre.get(index);
            user.getHomePage().getRecommendations().add(recommended);
            this.message = "The recommendations for user " + this.getUsername() + " have been updated successfully.";
            return;
        }

        if (this.recommendationType.equals("random_playlist")) {
            user.simulate(this.getTimestamp(), database);
            HashSet<SongInput> songs = new HashSet<>();
            songs.addAll(user.getFavourites().getSongs());
            for (Playlist playlist : user.getMyPlaylists()) {
                songs.addAll(playlist.getSongs());
            }
            for (Playlist playlist : user.getFollowed()) {
                songs.addAll(playlist.getSongs());
            }

            HashMap<String, Integer> genres = new HashMap<>();
            for (SongInput songInput : songs) {
                if (genres.containsKey(songInput.getGenre())) {
                    Integer previousSongs = genres.remove(songInput.getGenre());
                    genres.put(songInput.getGenre(), previousSongs + 1);
                } else {
                    genres.put(songInput.getGenre(), 1);
                }
            }

            List <Map.Entry<String, Integer>> topGenres =
                    genres.entrySet().stream().sorted(new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue() - o1.getValue();
                }
            }).toList();

            if (topGenres.isEmpty()) {
                this.message = "No new recommendations were found";
                return;
            }

            Playlist playlist = new Playlist();

            String genre1 = topGenres.get(0).getKey();
            playlist.getSongs().addAll(this.getRecommendationByGenre(database, 5, genre1));

            if (topGenres.size() > 1) {
                String genre2 = topGenres.get(1).getKey();
                playlist.getSongs().addAll(this.getRecommendationByGenre(database, 3, genre2));
            }

            if (topGenres.size() > 2) {
                String genre3 = topGenres.get(2).getKey();
                playlist.getSongs().addAll(this.getRecommendationByGenre(database, 2, genre3));
            }

            playlist.setName(this.getUsername() + "'s recommendations");
            user.getHomePage().getRecommendations().add(playlist);

            this.message = "The recommendations for user " + this.getUsername() + " have been updated successfully.";
        }
    }

    @Override
    public void convertToObjectNode(ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
