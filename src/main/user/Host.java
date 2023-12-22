package main.user;

import fileio.input.PodcastInput;
import lombok.Getter;
import main.Database;
import pages.HostPage;

import main.Notification;
import java.util.ArrayList;

public class Host extends User implements ContentCreator, ObservableUser {
    /**
     * constructor
     * @param username username of new host
     * @param age age of new host
     * @param city city of new host
     */
    public Host(final String username, final int age, final String city) {
        super(username, age, city);
    }

    @Getter
    private final ArrayList<PodcastInput> podcasts = new ArrayList<PodcastInput>();
    @Getter
    private final HostPage hostPage = new HostPage(this);

    @Getter
    private final ArrayList<User> subscribers = new ArrayList<>();

    @Override
    public String getCreatorName() {
        return this.getUsername();
    }

    @Override
    public void subscribe(User user) {
        this.subscribers.add(user);
        user.getSubscriptions().add(this);
    }

    @Override
    public void unsubscribe(User user) {
        this.subscribers.remove(user);
        user.getSubscriptions().remove(this);
    }

    @Override
    public void notify(Notification newNotification) {
        for (User user: this.subscribers) {
            user.update(newNotification);
        }
    }

    /**
     * method checks if host contains specific podcast given by name in his podcast list
     * @param podcastName name of searched podcast
     * @return true if podcast exists in list; false otherwise
     */
    public boolean containsPodcastName(final String podcastName) {
        for (PodcastInput podcast: this.podcasts) {
            if (podcast.getName().equals(podcastName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * method searches specific podcast given by name in host's podcast list
     * @param podcastName name of searched podcast
     * @return found podcast if existent; null otherwise
     */
    public PodcastInput getPodcastByName(final String podcastName) {
        for (PodcastInput podcast : this.podcasts) {
            if (podcast.getName().equals(podcastName)) {
                return podcast;
            }
        }
        return null;
    }

    /**
     * inner class that serves announcement type, specific to host
     */
    public static class Announcement {
        @Getter
        private final String name;
        @Getter
        private final String description;

        /**
         * constructor
         * @param name name of new announcement
         * @param description description of new announcement
         */
        public Announcement(final String name, final String description) {
            this.name = name;
            this.description = description;
        }
    }

    @Getter
    private final ArrayList<Announcement> announcements = new ArrayList<Announcement>();

    /**
     * method checks if host contains specific announcement given by name in his announcement list
     * @param announcementName the name of the searched announcement
     * @return true if announcement was found; false otherwise
     */
    public boolean checkAnnouncement(final String announcementName) {
        for (Announcement announcement : announcements) {
            if (announcement.name.equals(announcementName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * method searches specific announcement by name in host's announcement list
     * @param announcementName name of the searched announcement
     * @return found announcement if existent; null otherwise
     */
    public Announcement findAnnouncement(final String announcementName) {
        for (Announcement announcement : announcements) {
            if (announcement.getName().equals(announcementName)) {
                return announcement;
            }
        }
        return null;
    }

    /**
     * method checks if host could be deleted; firstly, it checks if any other user listens to
     * any playlist created by host;
     * then it checks if any podcast is listened to by any user; then it checks if any user is
     * currently on host's page
     * @param timestamp timestamp of check, used to update users' status to this moment
     * @param database extended input library
     * @return true if host could be deleted; false otherwise
     */
    @Override
    public boolean check(final int timestamp, final Database database) {
        if (!super.check(timestamp, database)) {
            return false;
        }
        for (PodcastInput podcast: this.podcasts) {
            if (!podcast.check(timestamp, database)) {
                return false;
            }
        }
        for (User user: database.getUsers()) {
            if (user.getCurrentPage() == this.hostPage) {
                return false;
            }
        }
        return true;
    }

    /**
     * method clears host's contribution to the global database;
     * method clears all playlists created by host;
     * then method clears all podcasts created by host;
     * @param database extended input library
     */
    @Override
    public void clear(final Database database) {
        super.clear(database);
        for (PodcastInput podcastInput: podcasts) {
            podcastInput.clear(database);
            database.getPodcasts().remove(podcastInput);
        }
    }
}
