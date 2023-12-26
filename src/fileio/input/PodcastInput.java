package fileio.input;

import main.Database;
import main.Wrappeable;
import main.user.User;

import java.util.ArrayList;
import java.util.Objects;

public final class PodcastInput implements Wrappeable {
    private String name;
    private String owner;
    private ArrayList<EpisodeInput> episodes;

    public PodcastInput() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PodcastInput that = (PodcastInput) o;

        if (!name.equals(that.name)) return false;
        if (!Objects.equals(owner, that.owner)) return false;
        return episodes.equals(that.episodes);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + episodes.hashCode();
        return result;
    }

    @Override
    public String extractName() {
        return this.name;
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
