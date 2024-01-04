package main.user;

import main.Notification;

public interface ObserveContentCreators {
    /**
     * method updates observer's notification list
     * @param newNotification notification to be added
     */
    void update(Notification newNotification);
}
