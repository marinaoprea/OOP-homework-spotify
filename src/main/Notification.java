package main;

import lombok.Getter;
import main.user.User;

import java.util.Locale;

public final class Notification {
    @Getter
    private final String description;
    @Getter
    private final String name;
    public Notification(final String content, final User user) {
        this.description = "New " + content + " from " + user.getUsername() + ".";
        this.name = "New " + content.substring(0, 1).toUpperCase() + content.substring(1);
    }
}
