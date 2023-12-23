package pages.pageNavigation;

import main.Database;
import main.user.User;

public class ChangeLikedContent implements ChangePage {
    @Override
    public String execute(Database database, User user) {
        user.setCurrentPage(user.getLikedContent());
        return user.getUsername() + " accessed LikedContent successfully.";
    }
}
