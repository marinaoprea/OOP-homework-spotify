package pages.pageNavigation;

import main.Database;
import main.user.User;

public interface ChangePage {
    String execute(Database database, User user);
}
