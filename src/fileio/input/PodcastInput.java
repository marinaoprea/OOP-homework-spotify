package fileio.input;

import main.Database;
import main.user.User;

import java.util.ArrayList;

public final class PodcastInput {
    private String name;
    private String owner;
    private ArrayList<EpisodeInput> episodes;

    public PodcastInput() {
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public ArrayList<EpisodeInput> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(final ArrayList<EpisodeInput> episodes) {
        this.episodes = episodes;
    }

    /**
     * method checks if podcast could be deleted;
     * that is if no user listens to podcast at given timestamp
     * @param timestamp timestamp when the check is performed
     * @param database extended input library
     * @return true if podcast could be deleted; false otherwise
     */
    public boolean check(final int timestamp, final Database database) {
        for (User user: database.getUsers()) {
            user.simulate(timestamp, database);
            String source = user.getLoadedSourceName();
            if (source.equals(this.name) && user.getTimeLoaded() != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * method clears podcast from user's saved history;
     * @param database extended input library
     */
    public void clear(final Database database) {
        for (User user: database.getUsers()) {
            User.SavedHistory savedHistory = user.findInHistory(this.getName());
            if (savedHistory != null) {
                user.getHistory().remove(savedHistory);
            }
        }
    }
}
