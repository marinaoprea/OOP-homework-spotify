package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.CommandInput;
import main.Database;
import main.Notification;
import main.user.User;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class GetNotifications extends Command {

    private ArrayList<Notification> notifications;
    public GetNotifications(final CommandInput commandInput) {
        super(commandInput);
    }

    @Override
    public void execute(Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            return;
        }
        notifications = new ArrayList<>(user.getNotifications());
        user.getNotifications().clear();
    }

    @Override
    public void convertToObjectNode(ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);

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
