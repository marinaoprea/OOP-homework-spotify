package pages.pageNavigation;

import main.Database;
import main.user.User;
import pages.Page;

import java.util.LinkedList;

public final class NavigationInvoker {
    private final LinkedList<Page> history = new LinkedList<>();
    private final LinkedList<Page> undo = new LinkedList<>();

    /**
     * method calls for execution method overriden in every subclass;
     * thus execution is performed based on dynamic type of Change Page command;
     * in case of successful execution, previous accessed page is added to the history and
     * undo history is cleared
     * @param changePage change page to be executed
     * @param database extended input library
     * @param user user that performed change page
     * @return completion message
     */
    public String execute(final ChangePage changePage, final Database database, final User user) {
        Page previousPage = user.getCurrentPage();
        String message = changePage.execute(database, user);
        if (!message.contains("non-existent")) {
            history.add(previousPage);
            undo.clear();
        }
        return message;
    }

    /**
     * method performs undo in page navigation;
     * method firstly checks if there are any pages to go back to;
     * method sets user's current page to last page in history, removes page from navigation
     * history and adds page to undo history
     * @param user user that performed undo
     * @return completion message
     */
    public String undo(final User user) {
        if (this.history.isEmpty()) {
            return "There are no pages left to go back.";
        }

        Page previous = history.getLast();
        undo.add(user.getCurrentPage());
        user.setCurrentPage(previous);
        history.remove(previous);
        return "The user " + user.getUsername()
                + " has navigated successfully to the previous page.";
    }

    /**
     * method performs redo in page navigation;
     * method firstly checks if there are any pages to go forward to;
     * method sets user's current page to last page in undo history;
     * method removes page from undo history and adds it to navigation history
     * @param user user that performed redo
     * @return completion method
     */
    public String redo(final User user) {
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
