package commands.removals;

import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import main.CommandInput;
import main.Database;
import main.user.Host;
import main.user.User;

public final class RemoveAnnouncement extends Command {
    private String message;
    private final String name;
    public RemoveAnnouncement(final CommandInput commandInput) {
        super(commandInput);
        this.name = commandInput.getName();
    }

    /**
     * method sets corresponding error messages;
     * method checks if user exists, if user's a host, if user has an announcement with
     * given name;
     * if valid, announcement is removed from host's list
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (!database.findHostByName(this.getUsername())) {
            this.message = this.getUsername() + " is not a host.";
            return;
        }
        Host host = (Host) user;
        if (!host.checkAnnouncement(this.name)) {
            this.message = this.getUsername() + " has no announcement with the given name.";
            return;
        }
        Host.Announcement newAnnouncement = host.findAnnouncement(this.name);
        host.getAnnouncements().remove(newAnnouncement);
        this.message = this.getUsername() + " has successfully deleted the announcement.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
