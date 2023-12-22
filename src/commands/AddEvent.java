package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Notification;
import main.user.Artist;
import main.CommandInput;
import main.Database;
import main.user.User;

public final class AddEvent extends Command {
    private String message;
    private final String name;
    private final String description;
    private final String date;
    public AddEvent(final CommandInput commandInput) {
        super(commandInput);
        this.name = commandInput.getName();
        this.description = commandInput.getDescription();
        this.date = commandInput.getDate();
    }

    /**
     * method prints corresponding error message;
     * method checks if user is an artist, if artist has not an event with the same name,
     * if event has a valid date
     * if valid, method adds new event in artist's event list
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (!(user instanceof Artist artist)) {
            this.message = this.getUsername() + " is not an artist.";
            return;
        }
        if (artist.findEventByName(this.name)) {
            this.message = this.getUsername() + " has another event with the same name.";
            return;
        }
        Artist.Event event = new Artist.Event(this.name, this.date, this.description);
        if (!event.checkDate()) {
            this.message = "Event for " + this.getUsername() + " does not have a valid date.";
            return;
        }
        this.message = this.getUsername() + " has added new event successfully.";
        artist.getEvents().add(event);

        main.Notification notification = new Notification("event", artist);
        artist.notify(notification);
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
