package pages.pageNavigation;

import main.Database;
import main.user.User;

public final class ChangeHome implements ChangePage {
    /**
     * method returns corresponding message
     * method changes user's current page to his home page
     * @param database extended input library
     * @param user user to change page
     * @return completion message
     */
    @Override
    public String execute(final Database database, final User user) {
        user.setCurrentPage(user.getHomePage());
        return user.getUsername() + " accessed Home successfully.";
    }
}
