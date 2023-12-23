package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;
import pages.pageNavigation.ChangePageFactory;
import pages.pageNavigation.NavigationInvoker;

public final class ChangePage extends Command {
    private String message;
    private final String nextPage;
    public ChangePage(final CommandInput commandInput) {
        super(commandInput);
        this.nextPage = commandInput.getNextPage();
    }

    /**
     * method sets corresponding error messages;
     * method checks if user is normal user;
     * method updates user's current page according to the request
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username" + this.getUsername() + " doesn't exist";
            return;
        }
        if (!database.getNormalUsers().contains(user)) {
            this.message = this.getUsername() + " is not a normal user.";
            return;
        }
        /*if (this.nextPage.equals("Home")) {
            user.setCurrentPage(user.getHomePage());
            this.message = this.getUsername() + " accessed Home successfully.";
            return;
        }
        if (this.nextPage.equals("LikedContent")) {
            user.setCurrentPage(user.getLikedContent());
            this.message = this.getUsername() + " accessed LikedContent successfully.";
            return;
        }
        this.message = this.getUsername() + " is trying to access a non-existent page.";*/
        pages.pageNavigation.ChangePage changePage = ChangePageFactory.getChangePage(this.nextPage);
        this.message = user.getNavigation().execute(changePage, database, user);
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
