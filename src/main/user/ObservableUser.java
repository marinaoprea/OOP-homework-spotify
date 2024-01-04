package main.user;

import main.Notification;

public interface ObservableUser {
    /**
     * method notifies all subscribers that new notification was added
     * @param newNotification new notification added
     */
    void notify(Notification newNotification);
}
