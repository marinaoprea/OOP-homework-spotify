package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;

public final class CommandInvoker {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * method invokes execution of command and returns command output conerted
     * to object node
     * @param database extended input library
     * @param command command to be executed
     * @return command output as object node
     */
    public static ObjectNode invokeCommand(final Database database, final Command command) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        command.execute(database);
        command.convertToObjectNode(objectNode);
        return objectNode;
    }
}
