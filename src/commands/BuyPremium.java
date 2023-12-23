package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.user.User;

public class BuyPremium extends Command {
    private String message;

    public BuyPremium(final CommandInput commandInput) {
        super(commandInput);
    }

    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        if (user.isPremium()) {
            this.message = this.getUsername() + " is already a premium user.";
            return;
        }

        user.simulate(this.getTimestamp(), database);
        user.getSongHistory().clear();
        user.setPremium(true);

        if (user.getSelectedType().equals("song")) {
            SongInput song = user.getSongFromUser(database);
            if (song != null) {
                user.getSongHistory().add(song);
            }
        }
        if (user.getSelectedType().equals("playlist")) {
            SongInput song = user.getSongFromUserInPlaylist(database);
            if (song != null) {
                user.getSongHistory().add(song);
            }
        }
        if (user.getSelectedType().equals("album")) {
            SongInput song = user.getSongFromUserInAlbum(database);
            if (song != null) {
                user.getSongHistory().add(song);
            }
        }

        this.message = this.getUsername() + " bought the subscription successfully.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", message);
    }
}
