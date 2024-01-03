package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.Notification;
import main.user.User;

import java.util.ArrayList;

public final class GetNotifications extends Command {

    private ArrayList<Notification> notifications;
    private String message;
    public GetNotifications(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method searches user in database and sets notification list as user's notification
     * list
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            this.message = "The username " + this.getUsername() + " doesn't exist.";
            return;
        }
        notifications = new ArrayList<>(user.getNotifications());
        user.getNotifications().clear();
    }

    /**
     * @param objectNode created ObjectNode
     */
    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        if (this.message != null) {
            objectNode.put("message", this.message);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNotifications = mapper.createArrayNode();
        for (Notification notification: notifications) {
            ObjectNode notificationObjectNode = mapper.createObjectNode();
            notificationObjectNode.put("name", notification.getName());
            notificationObjectNode.put("description", notification.getDescription());

            arrayNotifications.add(notificationObjectNode);
        }

        objectNode.put("notifications", arrayNotifications);
    }
}
