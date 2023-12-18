package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;

public class Command {
    private String command;
    private String username;
    private int timestamp;

    /**
     * constructor that creates new generic command from input command
     * @param commandInput the input command we take information from
     */
    public Command(final CommandInput commandInput) {
        this.command = commandInput.getCommand();
        this.username = commandInput.getUsername();
        this.timestamp = commandInput.getTimestamp();
    }

    /**
     * getter for command name
     * @return command name
     */
    public String getCommand() {
        return command;
    }

    /**
     * setter for command name
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * getter for username of user who commanded
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * getter for timestamp at which command was given
     * @return timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * setter for timestamp at which command was given
     * @param timestamp time from the beginning of the test at which command was given
     */
    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * setter for username of the user that gave the command
     * @param username of the user
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * function that executes given command;
     * made for inheritance;
     * in case of no downcast prints error message
     * @param database extended input library
     */
    public void execute(final Database database) {
        System.out.println("Command unavailable");
    }

    /**
     * method converts generic command fields to json ObjectNode
     * @param objectNode created ObjectNode
     */
    public void convertToObjectNode(final ObjectNode objectNode) {
        objectNode.put("command", this.getCommand());
        objectNode.put("user", this.getUsername());
        objectNode.put("timestamp", this.getTimestamp());
    }
}
