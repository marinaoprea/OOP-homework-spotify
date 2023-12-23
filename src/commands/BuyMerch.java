package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.Artist;
import main.user.User;
import pages.ArtistPage;

public class BuyMerch extends Command {

    private String name;
    private String message;

    public BuyMerch(CommandInput commandInput) {
        super(commandInput);
        this.name = commandInput.getName();
    }

    @Override
    public void execute(Database database) {
       User user = database.findUserInDatabase(this.getUsername());
       if (user == null) {
           this.message = "The username " + this.getUsername() + " doesn't exist.";
           return;
       }
       if (!(user.getCurrentPage() instanceof ArtistPage)) {
           this.message = "Cannot buy merch from this page.";
           return;
       }

       Artist artist = (Artist) user.getCurrentPage().getOwner();
       if (!artist.findMerchByName(this.name)) {
           this.message = "The merch " + this.name + " doesn't exist.";
           return;
       }

       Artist.Merch merch = artist.findMerch(this.name);
       user.getBoughtMerch().add(merch);
       Double previousMerchRevenue = artist.getRevenue().getMerchRevenue();
       artist.getRevenue().setMerchRevenue(previousMerchRevenue + merch.getPrice());
       artist.getRevenue().setWasListened(true);
       this.message = this.getUsername() + " has added new merch successfully.";
    }

    @Override
    public void convertToObjectNode(ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
