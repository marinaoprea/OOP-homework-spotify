package main.user;

public interface ContentCreator {
    String getCreatorName();
    void subscribe(User user);
    void unsubscribe(User user);
}
