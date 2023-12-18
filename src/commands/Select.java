package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.user.Artist;
import main.user.Host;
import main.user.User;

public final class Select extends Command {
    private final int itemNumber;
    private String message;
    public Select(final CommandInput commandInput) {
        super(commandInput);
        this.itemNumber = commandInput.getItemNumber();
    }

    /**
     * method prints corresponding error messages and sets
     * selected index in search results and marks user as having selected
     * something
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "User does not exist.";
            return;
        }
        if (user.getLastSearch() == null || user.isLoaded()) {
            this.message = "Please conduct a search before making a selection.";
            return;
        }
        SearchBar search = user.getLastSearch();
        if (this.itemNumber > search.getResults().size()) {
            this.message = "The selected ID is too high.";
            return;
        }
        user.setIsSelected(true);
        user.setSelectedIndex(this.itemNumber);
        if (user.getLastSearch().getType().equals("artist")) {
            this.message = "Successfully selected " + search.getResults().get(this.itemNumber - 1)
                    + "'s page.";
            Artist artist = database.findArtist(search.getResults().get(this.itemNumber - 1));
            if (artist == null) {
                return;
            }
            user.setCurrentPage(artist.getArtistPage());
            return;
        }
        if (user.getLastSearch().getType().equals("host")) {
            this.message = "Successfully selected " + search.getResults().get(this.itemNumber - 1)
                    + "'s page.";
            Host host = database.findHost(search.getResults().get(this.itemNumber - 1));
            if (host == null) {
                return;
            }
            user.setCurrentPage(host.getHostPage());
            return;
        }
        this.message = "Successfully selected "
                + search.getResults().get(this.itemNumber - 1) + ".";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
