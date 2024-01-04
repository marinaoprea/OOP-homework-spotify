package pages.pageNavigation;

import main.Database;
import main.user.User;

public interface ChangePage {
    /**
     * executes change page
     * @param database extended input library
     * @param user user that performed change page
     * @return completion message
     */
    String execute(Database database, User user);
}
