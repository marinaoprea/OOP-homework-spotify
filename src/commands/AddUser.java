package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.user.User;
import main.user.Artist;
import main.user.Host;
import main.Database;

public final class AddUser extends Command {
    private String message;
    private final int age;
    private final String city;
    private final String type;
    public AddUser(final CommandInput commandInput) {
        super(commandInput);
        this.age = commandInput.getAge();
        this.city = commandInput.getCity();
        this.type = commandInput.getType();
    }

    /**
     * method sets corresponding error messages;
     * method checks if username is already taken;
     * method adds instantiates user with corresponding type;
     * user is added both to global list of users in database and to corresponding list
     * depending on his type
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user != null) {
            this.message = "The username " + this.getUsername() + " is already taken.";
            return;
        }
        this.message = "The username " + this.getUsername() + " has been added successfully.";
        if (this.type.equals("user")) { // normal user
            User newUser = new User(this.getUsername(), this.age, this.city);
            database.getUsers().add(newUser);
            database.getNormalUsers().add(newUser);
            return;
        }
        if (this.type.equals("artist")) {
            Artist artist = database.findArtist(this.getUsername());
            if (artist != null) {
                database.getArtists().remove(artist);
            }
            Artist newArtist = new Artist(this.getUsername(), this.age, this.city);
            database.getUsers().add(newArtist);
            database.getArtists().add(newArtist);
            return;
        }
        if (this.type.equals("host")) {
            Host newHost = new Host(this.getUsername(), this.age, this.city);
            database.getUsers().add(newHost);
            database.getHosts().add(newHost);
            return;
        }
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
