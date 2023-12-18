package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.Stats;
import main.Playlist;
import main.Album;
import main.user.User;

public class Status extends Command {
    private final Stats stats = new Stats();
    public Status(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method performs statistics for current listened song;
     * before that, simulation up until this moment is performed;
     * @param database extended library input
     * @param user user that gave status command
     */
    private void statusSong(final Database database, final User user) {
        user.simulate(this.getTimestamp(), database);
        SongInput song = user.getSongFromUser(database);

        if (song != null) {
            this.stats.setName(song.getName());
            this.stats.setRemainedTime(song.getDuration() - user.getTimeRelativeToSong());
        } else {
            this.stats.setRemainedTime(0);
            this.stats.setName("");
        }

        switch (user.getRepeat()) {
            case 0:
                this.stats.setRepeat("No Repeat");
                break;
            case 1:
                this.stats.setRepeat("Repeat Once");
                break;
            case 2:
                this.stats.setRepeat("Repeat Infinite");
                break;
            default:
                break;
        }

        this.stats.setShuffle(user.getShuffle());
        this.stats.setPaused(!user.isPlaying());
    }

    /**
     * method performs statistics for current listened podcast;
     * before that, simulation up until this moment is performed;
     * @param database extended library input
     * @param user user that gave status command
     */
    private void statusPodcast(final Database database, final User user) {
        user.simulate(this.getTimestamp(), database);
        int index = user.getSelectedIndex();
        String podcastName;
        if (index > 0 && index <= user.getLastSearch().getResults().size()) {
            podcastName = user.getLastSearch().getResults().get(index - 1);
        } else {
            this.stats.setName("");
            this.stats.setPaused(!user.isPlaying());
            this.stats.setShuffle(user.getShuffle());
            this.stats.setRemainedTime(0);
            switch (user.getRepeat()) {
                case 0:
                    this.stats.setRepeat("No Repeat");
                    break;
                case 1:
                    this.stats.setRepeat("Repeat Once");
                    break;
                case 2:
                    this.stats.setRepeat("RepeatInfinite");
                    break;
                default:
                    break;
            }
            return;
        }
        PodcastInput podcast = database.findPodcast(podcastName);
        int indexInList = user.getSelectedIndexInList();
        if (indexInList > 0 && indexInList <= podcast.getEpisodes().size()) {
            this.stats.setName(podcast.getEpisodes().get(indexInList - 1).getName());
        } else {
            this.stats.setName("");
        }

        if (!this.stats.getName().isEmpty()) {
            this.stats.setRemainedTime(podcast.getEpisodes().get(indexInList - 1).getDuration()
                    - user.getTimeRelativeToSong());
        } else {
            this.stats.setRemainedTime(0);
        }

        this.stats.setShuffle(user.getShuffle());
        this.stats.setPaused(!user.isPlaying());

        switch (user.getRepeat()) {
            case 0:
                this.stats.setRepeat("No Repeat");
                break;
            case 1:
                this.stats.setRepeat("Repeat Once");
                break;
            case 2:
                this.stats.setRepeat("RepeatInfinite");
                break;
            default:
                break;
        }
    }

    /**
     * method performs statistics for current listened playlist;
     * before that, simulation up until this moment is performed;
     * @param database extended library input
     * @param user user that gave status command
     */
    private void statusPlaylist(final Database database, final User user) {
        user.simulate(this.getTimestamp(), database);
        int index = user.getSelectedIndex();
        String playlistName = "";
        if (index > 0 && index <= user.getLastSearch().getResults().size()) {
            playlistName = user.getLastSearch().getResults().get(index - 1);
        } else {
            this.stats.setName("");
            this.stats.setPaused(!user.isPlaying());
            this.stats.setShuffle(user.getShuffle());
            this.stats.setRemainedTime(0);

            switch (user.getRepeat()) {
                case 0:
                    this.stats.setRepeat("No Repeat");
                    break;
                case 1:
                    this.stats.setRepeat("Repeat All");
                    break;
                case 2:
                    this.stats.setRepeat("Repeat Current Song");
                    break;
                default:
                    break;
            }
            return;
        }
        Playlist playlist = database.findPlaylistInDatabase(playlistName);
        int indexInList = user.getSelectedIndexInList();
        if (indexInList > 0 && indexInList <= playlist.getSongs().size()) {
            this.stats.setName(playlist.getSongs().get(indexInList - 1).getName());
        } else {
            this.stats.setName("");
        }

        if (!this.stats.getName().isEmpty()) {
            this.stats.setRemainedTime(playlist.getSongs().get(indexInList - 1).getDuration()
                    - user.getTimeRelativeToSong());
        } else {
            this.stats.setRemainedTime(0);
        }

        this.stats.setShuffle(user.getShuffle());
        this.stats.setPaused(!user.isPlaying());
        switch (user.getRepeat()) {
            case 0:
                this.stats.setRepeat("No Repeat");
                break;
            case 1:
                this.stats.setRepeat("Repeat All");
                break;
            case 2:
                this.stats.setRepeat("Repeat Current Song");
                break;
            default:
                break;
        }
    }

    /**
     * method performs statistics for current listened album;
     * before that, simulation up until this moment is performed;
     * @param database extended library input
     * @param user user that gave status command
     */
    private void statusAlbum(final Database database, final User user) {
        user.simulate(this.getTimestamp(), database);
        int index = user.getSelectedIndex();
        String albumName = user.getLoadedSourceName();
        if (albumName.isEmpty()) {
            this.stats.setName("");
            this.stats.setPaused(!user.isPlaying());
            this.stats.setShuffle(user.getShuffle());
            this.stats.setRemainedTime(0);

            switch (user.getRepeat()) {
                case 0:
                    this.stats.setRepeat("No Repeat");
                    break;
                case 1:
                    this.stats.setRepeat("Repeat All");
                    break;
                case 2:
                    this.stats.setRepeat("Repeat Current Song");
                    break;
                default:
                    break;
            }
            return;
        }
        Album album = database.findAlbum(albumName);
        int indexInList = user.getSelectedIndexInList();
        if (indexInList > 0 && indexInList <= album.getSongs().size()) {
            this.stats.setName(album.getSongs().get(indexInList - 1).getName());
        } else {
            this.stats.setName("");
        }

        if (!this.stats.getName().isEmpty()) {
            this.stats.setRemainedTime(album.getSongs().get(indexInList - 1).getDuration()
                    - user.getTimeRelativeToSong());
        } else {
            this.stats.setRemainedTime(0);
        }

        this.stats.setShuffle(user.getShuffle());
        this.stats.setPaused(!user.isPlaying());
        switch (user.getRepeat()) {
            case 0:
                this.stats.setRepeat("No Repeat");
                break;
            case 1:
                this.stats.setRepeat("Repeat All");
                break;
            case 2:
                this.stats.setRepeat("Repeat Current Song");
                break;
            default:
                break;
        }
    }

    /**
     * method checks if source was loaded and calls for specific status methods
     * regarding the type of the loaded audio file
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            return;
        }
        if (!user.isLoaded()) {
            this.stats.setPaused(false);
            this.stats.setPaused(true);
            this.stats.setName("");
            this.stats.setRepeat("No Repeat");
            this.stats.setShuffle(false);
            return;
        }

        if (user.getLastSearch().getType().equals("song")) {
            this.statusSong(database, user);
            return;
        }
        if (user.getLastSearch().getType().equals("podcast")) {
            this.statusPodcast(database, user);
            return;
        }
        // for playlist
        if (user.getLastSearch().getType().equals("playlist")) {
            this.statusPlaylist(database, user);
            return;
        }
        if (user.getLastSearch().getType().equals("album")) {
            this.statusAlbum(database, user);
            return;
        }
    }

    /**
     * overrides conversion to this specific command
     * @param objectNode created ObjectNode
     */
    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode statsObject = mapper.createObjectNode();
        statsObject.put("name", this.stats.getName());
        statsObject.put("remainedTime", this.stats.getRemainedTime());
        statsObject.put("repeat", this.stats.getRepeat());
        statsObject.put("shuffle", this.stats.isShuffle());
        statsObject.put("paused", this.stats.isPaused());
        objectNode.put("stats", statsObject);
    }
}
