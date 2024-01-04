package pages;

import commands.Constants;
import fileio.input.SongInput;
import lombok.Getter;
import main.Playlist;
import main.Recommendation;
import main.user.User;

import java.util.LinkedList;
import java.util.List;

public final class HomePage extends Page implements VisitablePage {
    @Getter
    private final LinkedList<Recommendation> recommendations = new LinkedList<>();
    public HomePage(final User owner) {
        super(owner);
    }
    @Override
    public String accept(final PageVisitor pageVisitor) {
        return pageVisitor.visitPage(this);
    }

    /**
     * method calculates song recommendations at call moment;
     * recommended songs are liked songs by page owner;
     * results are limited to 5
     * @return list of recommended songs
     */
    public List<SongInput> generateSongRecommendations() {
        return this.getOwner().getFavourites().getSongs().stream().sorted(
                (o1, o2) -> o2.getNoLikes() - o1.getNoLikes())
                .limit(Constants.NO_SEARCH_RESULTS).toList();
    }

    /**
     * method calculates playlist recommendations at call moment;
     * recommended playlists are followed playlists by page owner;
     * results are limited to 5
     * @return list of recommended playlists
     */
    public List<Playlist> generatePlaylistRecommendations() {
        return this.getOwner().getFollowed().stream().sorted(
                (o1, o2) -> o2.getAllLikes() - o1.getAllLikes())
                .limit(Constants.NO_SEARCH_RESULTS).toList();
    }

    /**
     * returns list of recommendations that are songs
     */
    public List<Recommendation> generateSongRecommendationsFromUpdate() {
        return this.recommendations.stream().filter(o1 -> o1.getType().equals("song")).toList();
    }

    /**
     * returns list of recommendations that are playlist
     */
    public List<Recommendation> generatePlaylistRecommendationsFromUpdate() {
        return this.recommendations.stream().filter(o1 -> o1.getType().equals("playlist")).toList();
    }
}
