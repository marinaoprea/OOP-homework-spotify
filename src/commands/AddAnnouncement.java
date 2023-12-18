package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.Host;
import main.user.User;

public final class AddAnnouncement extends Command {
    private String message;
    private final String name;
    private final String description;
    public AddAnnouncement(final CommandInput commandInput) {
        super(commandInput);
        this.name = commandInput.getName();
        this.description = commandInput.getDescription();
    }

    /**
     * method prints corresponding error message;
     * method checks if user is host, if host has not an announcement with the same name;
     * if valid, method adds new announcements in host's announcement list
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
        if (host.checkAnnouncement(this.name)) {
            this.message = this.getUsername() + " has already added an announcement with this name.";
            return;
        }
        Host.Announcement newAnnouncement = new Host.Announcement(this.name, this.description);
        host.getAnnouncements().add(newAnnouncement);
        this.message = this.getUsername() + " has successfully added new announcement.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
