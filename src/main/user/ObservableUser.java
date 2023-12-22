package main.user;

import main.Notification;

public interface ObservableUser {
    void notify(final Notification newNotification);
}
