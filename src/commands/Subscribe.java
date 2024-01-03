package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.ContentCreator;
import main.user.User;
import pages.ArtistPage;
import pages.HostPage;

public final class Subscribe extends Command {
    private String message;

    public Subscribe(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sets corresponding error messages;
     * method checks if user is subscribed to the content creator; if so, unsubscribe is performed;
     * otherwise new subscription is added in user's list and new subscriber is added in content creator's
     * list
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (!user.getSelectedType().equals("artist") && !user.getSelectedType().equals("host")) {
            this.message = "You can subscribe only to artists and hosts.";
            return;
        }
        if (!(user.getCurrentPage() instanceof ArtistPage) && !(user.getCurrentPage() instanceof HostPage)) {
            this.message = "To subscribe you need to be on the page of an artist or host.";
            return;
        }
        // verified cast
        ContentCreator contentCreator = (ContentCreator) user.getCurrentPage().getOwner();
        if (user.getSubscriptions().contains(contentCreator)) {
            contentCreator.unsubscribe(user);
            this.message = this.getUsername() + " unsubscribed from " + contentCreator.getCreatorName() + " successfully.";
        } else {
            contentCreator.subscribe(user);
            this.message = this.getUsername() + " subscribed to " + contentCreator.getCreatorName() + " successfully.";
        }
    }

    /**
     * @param objectNode created ObjectNode
     */
    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
