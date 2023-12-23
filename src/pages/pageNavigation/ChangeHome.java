package pages.pageNavigation;

import main.Database;
import main.user.User;

public class ChangeHome implements ChangePage {
    @Override
    public String execute(Database database, User user) {
        user.setCurrentPage(user.getHomePage());
        return user.getUsername() + " accessed Home successfully.";
    }
}
