package pages.pageNavigation;

import main.Database;
import main.user.User;
import pages.Page;

import java.util.LinkedList;

public class NavigationInvoker {
    private final LinkedList<Page> history = new LinkedList<>();
    private final LinkedList<Page> undo = new LinkedList<>();

    public String execute(ChangePage changePage, Database database, User user) {
        Page previousPage = user.getCurrentPage();
        String message = changePage.execute(database, user);
        if (!message.contains("non-existent")) {
            history.add(previousPage);
            undo.clear();
        }
        return message;
    }

    public String undo(Database database, User user) {
        if (this.history.isEmpty()) {
            return "There are no pages left to go back.";
        }

        Page previous = history.getLast();
        user.setCurrentPage(previous);
        undo.add(previous);
        history.remove(previous);
        return "The user " + user.getUsername() + " has navigated successfully to the previous page.";
    }

    public String redo(Database database, User user) {
        if (this.undo.isEmpty()) {
            return "There are no pages left to go forward.";
        }

        Page next = undo.getLast();
        user.setCurrentPage(next);
        undo.removeLast();
        history.add(next);
        return "The user " + user.getUsername() + " has navigated successfully to the next page.";
    }
}
