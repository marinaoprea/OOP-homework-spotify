package main.user;

public interface ContentCreator {
    /**
     * returns content creator's name
     */
    String getCreatorName();

    /**
     * adds subscription
     * @param user new subscriber
     */
    void subscribe(User user);

    /**
     * removes subscription
     * @param user subscriber to be removed
     */
    void unsubscribe(User user);
}
