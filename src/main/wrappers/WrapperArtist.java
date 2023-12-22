package main.wrappers;

import lombok.Getter;
import main.user.User;

import java.util.HashMap;

public class WrapperArtist implements ObserverWrapper{
    @Getter
    HashMap<User, Integer> topFans = new HashMap<>();

    @Override
    public void updateFans(User user, int listens) {
        if (!topFans.containsKey(user)) {
            topFans.put(user, listens);
            return;
        }
        Integer previousListens = topFans.remove(user);
        topFans.put(user, previousListens + listens);
    }
}
