package pages.pageNavigation;

import fileio.input.PodcastInput;
import main.Database;
import main.user.Host;
import main.user.User;

public class ChangeHost implements ChangePage {
    /**
     * method returns corresponding messages;
     * method gets listened host and changes user's page to host's page
     * @param database extended input library
     * @param user user to change page
     * @return completion message
     */
    @Override
    public String execute(final Database database, final User user) {
        if (user.getSelectedType().equals("artist") || user.getSelectedType().equals("song")
                || user.getSelectedType().equals("playlist")) {
            return user.getUsername() + " is trying to access a non-existent page.";
        }

        if (user.getSelectedType().equals("podcast")) {
            String podcastName = user.getLoadedSourceName();
            PodcastInput podcast = database.findPodcast(podcastName);
            if (podcast == null) {
                return user.getUsername() + " is trying to access a non-existent page.";
            }
            Host host = database.findHost(podcast.getOwner());
            if (host == null) {
                return user.getUsername() + " is trying to access a non-existent page.";
            }
            user.setCurrentPage(host.getHostPage());
            return user.getUsername() + " accessed Host successfully.";
        }

        return user.getUsername() + " is trying to access a non-existent page.";
    }
}
