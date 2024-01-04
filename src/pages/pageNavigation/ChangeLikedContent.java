package pages.pageNavigation;

import main.Database;
import main.user.User;

public class ChangeLikedContent implements ChangePage {

    /**
     * method returns corresponding message
     * method changes user's current page to his liked content page
     * @param database extended input library
     * @param user user to change page
     * @return completion message
     */
    @Override
    public String execute(final Database database, final User user) {
        user.setCurrentPage(user.getLikedContent());
        return user.getUsername() + " accessed LikedContent successfully.";
    }
}
