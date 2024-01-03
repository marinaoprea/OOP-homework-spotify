package commands.additions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import main.Notification;
import main.user.Artist;
import main.CommandInput;
import main.Database;
import main.user.User;

public final class AddMerch extends Command {
    private String message;
    private final int price;
    private final String description;
    private final String name;
    public AddMerch(final CommandInput commandInput) {
        super(commandInput);
        this.description = commandInput.getDescription();
        this.name = commandInput.getName();
        this.price = commandInput.getPrice();
    }

    /**
     * method prints corresponding error message;
     * method checks if user is artist, if host has merchandise with the same name;
     * if valid, method adds new merchandise in artist's list;
     * artist internal structure has changed; thus method calls for notification method
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
        if (artist.findMerchByName(this.name)) {
            this.message = this.getUsername() + " has merchandise with the same name.";
            return;
        }
        if (this.price < 0) {
            this.message = "Price for merchandise can not be negative.";
            return;
        }
        this.message = this.getUsername() + " has added new merchandise successfully.";
        artist.getArtistMerchList().add(new Artist.Merch(this.name, this.description, this.price));

        main.Notification notification = new Notification("Merchandise", artist);
        artist.notify(notification);
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
