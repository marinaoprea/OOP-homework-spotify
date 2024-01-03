package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;
import pages.pageNavigation.ChangePageFactory;

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
        pages.pageNavigation.ChangePage changePage = ChangePageFactory.getChangePage(this.nextPage);
        this.message = user.getNavigation().execute(changePage, database, user);
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
