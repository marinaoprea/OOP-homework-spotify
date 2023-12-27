package main.wrappers;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import lombok.Getter;
import main.Database;
import main.Wrappeable;
import main.user.User;

import java.util.HashMap;

public class WrapperHost implements ObserverWrapper {
    public WrapperHost() {

    }
    @Getter
    private final HashMap<User, Integer> topFans = new HashMap<>();
    @Getter
    private final HashMap<Wrappeable, Integer> wrapPodcasts = new HashMap<>();

    @Override
    public void updatePodcasts(final PodcastInput podcastInput, final EpisodeInput episode, final int listens, final Database database, final User user) {
        if (!wrapPodcasts.containsKey(episode)) {
            wrapPodcasts.put(episode, listens);
            return;
        }
        Integer previousListens = wrapPodcasts.remove(episode);
        wrapPodcasts.put(episode, previousListens + listens);
    }

    @Override
    public void updateFans(final User user, final int listens) {
        if (!topFans.containsKey(user)) {
            topFans.put(user, listens);
            return;
        }
        Integer previousListens = topFans.remove(user);
        topFans.put(user, previousListens + listens);
    }
}
