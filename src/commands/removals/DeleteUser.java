package commands.removals;

import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import main.CommandInput;
import main.Database;
import main.user.Artist;
import main.user.Host;
import main.user.User;

public final class DeleteUser extends Command {
    private String message;
    public DeleteUser(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method checks if user can be deleted; if so, method calls for
     * clearance method of user and then removes user from global list of users
     * in database;
     * methods check and clear base on user's dynamic type;
     * method sets corresponding command messages
     * @param database extended input database
     * @param user user to be deleted
     */
    private boolean deleteUser(final Database database, final User user) {
        if (!user.check(this.getTimestamp(), database)) {
            this.message = this.getUsername() + " can't be deleted.";
            return false;
        }
        user.clear(database);
        database.getUsers().remove(user);
        this.message = this.getUsername() + " was successfully deleted.";
        return true;
    }

    /**
     * method checks if username exists;
     * method calls for deletion method; if user has been deleted, we remove it
     * from the corresponding user list in database, based on user's type (normal user,
     * artist or host)
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        boolean deleted = deleteUser(database, user);
        if (!deleted) {
            return;
        }
        if (database.findArtistByName(this.getUsername())) { // user is artist
            database.getArtists().remove((Artist) user);
        }
        if (database.findHostByName(this.getUsername())) { // host
            database.getHosts().remove((Host) user);
        }
        // normal user
        database.getNormalUsers().remove(user);
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
