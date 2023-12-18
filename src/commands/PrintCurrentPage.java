package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.User;
import pages.PagePrinter;

public final class PrintCurrentPage extends Command {
    private String message;
    public  PrintCurrentPage(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method checks if user exists and if it is online;
     * method calls visitor page printer for current page
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            return;
        }
        if (!user.isConnectionStatus()) {
            this.message = this.getUsername() + " is offline.";
            return;
        }
        PagePrinter pagePrinter = new PagePrinter();
        this.message = user.getCurrentPage().accept(pagePrinter);
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
