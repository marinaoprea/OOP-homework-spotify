package commands.removals;

import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import main.CommandInput;
import main.Database;
import main.user.Artist;
import main.user.User;

public final class RemoveEvent extends Command {
    private String message;
    private final String name;
    public RemoveEvent(final CommandInput commandInput) {
        super(commandInput);
        this.name = commandInput.getName();
    }

    /**
     * method sets corresponding error messages;
     * method checks if user exists, if user's an artist, if user has an event with
     * given name;
     * if valid, event is removed from artist's list
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (!database.findArtistByName(this.getUsername())) {
            this.message = this.getUsername() + " is not an artist.";
            return;
        }
        Artist artist = (Artist) user;
        Artist.Event event = artist.getEvent(this.name);
        if (event == null) {
            this.message = this.getUsername() + " has no event with the given name.";
            return;
        }
        artist.getEvents().remove(event);
        this.message = this.getUsername() + " deleted the event successfully.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
