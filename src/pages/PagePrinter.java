package pages;

import fileio.input.PodcastInput;
import fileio.input.SongInput;
import main.Album;
import main.Recommendation;
import main.user.Artist;
import main.user.Host;
import main.Playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * class implements page visitor interface, providing implementation of page
 * printing methods corresponding to the dynamic type of pages;
 */
public final class PagePrinter implements PageVisitor {

    public PagePrinter() {

    }

    @Override
    public String visitPage(final Page page) {
        return "generic page";
    }
    @Override
    public String visitPage(final HomePage homePage) {
        StringBuilder ans = new StringBuilder("Liked songs:\n\t[");
        List<SongInput> songs = homePage.generateSongRecommendations();
        int i;
        for (i = 0; i < songs.size() - 1; i++) {
            ans.append(songs.get(i).getName());
            ans.append(", ");
        }
        if (!songs.isEmpty()) {
            ans.append(songs.get(i).getName());
        }
        ans.append("]\n\nFollowed playlists:\n\t[");
        List<Playlist> playlists = homePage.generatePlaylistRecommendations();
        for (i = 0; i + 1 < playlists.size(); i++) {
            ans.append(playlists.get(i).getName());
            ans.append(", ");
        }
        if (!playlists.isEmpty()) {
            ans.append(playlists.get(i).getName());
        }
        ans.append("]");

        ans.append("\n\nSong recommendations:\n\t[");
        List<Recommendation> songRecommendations =
                homePage.generateSongRecommendationsFromUpdate();
        for (i = 0; i < songRecommendations.size() - 1; i++) {
            ans.append(songRecommendations.get(i).getRecommendationName());
            ans.append(", ");
        }
        if (!songRecommendations.isEmpty()) {
            ans.append(songRecommendations.get(i).getRecommendationName());
        }
        ans.append("]");

        ans.append("\n\nPlaylists recommendations:\n\t[");
        List<Recommendation> playlistRecommendations =
                homePage.generatePlaylistRecommendationsFromUpdate();
        for (i = 0; i + 1 < playlistRecommendations.size(); i++) {
            ans.append(playlistRecommendations.get(i).getRecommendationName());
            ans.append(", ");
        }
        if (!playlistRecommendations.isEmpty()) {
            ans.append(playlistRecommendations.get(i).getRecommendationName());
        }
        ans.append("]");
        return ans.toString();
    }

    @Override
    public String visitPage(final LikedContent likedContent) {
        StringBuilder ans = new StringBuilder("Liked songs:\n\t[");
        int i;
        ArrayList<SongInput> songs = likedContent.getOwner().getFavourites().getSongs();
        for (i = 0; i + 1 < songs.size(); i++) {
            ans.append(songs.get(i).getName());
            ans.append(" - ");
            ans.append(songs.get(i).getArtist());
            ans.append(", ");
        }
        if (!songs.isEmpty()) {
            ans.append(songs.get(i).getName());
            ans.append(" - ");
            ans.append(songs.get(i).getArtist());
        }
        ans.append("]\n\nFollowed playlists:\n\t[");
        ArrayList<Playlist> playlists = likedContent.getOwner().getFollowed();
        for (i = 0; i + 1 < playlists.size(); i++) {
            ans.append(playlists.get(i).getName());
            ans.append(" - ");
            ans.append(playlists.get(i).getUser().getUsername());
            ans.append(", ");
        }
        if (!playlists.isEmpty()) {
            ans.append(playlists.get(i).getName());
            ans.append(" - ");
            ans.append(playlists.get(i).getUser().getUsername());
        }
        ans.append("]");
        return ans.toString();
    }

    @Override
    public String visitPage(final ArtistPage artistPage) {
        StringBuilder ans = new StringBuilder("Albums:\n\t[");
        ArrayList<Album> albums = ((Artist) artistPage.getOwner()).getAlbums();
        int i;
        for (i = 0; i + 1 < albums.size(); i++) {
            ans.append(albums.get(i).getName());
            ans.append(", ");
        }
        if (!albums.isEmpty()) {
            ans.append(albums.get(i).getName());
        }
        ans.append("]");

        ans.append("\n\nMerch:\n\t[");
        ArrayList<Artist.Merch> merchList = ((Artist) artistPage.getOwner()).getArtistMerchList();
        for (i = 0; i + 1 < merchList.size(); i++) {
            ans.append(merchList.get(i).getName());
            ans.append(" - ");
            ans.append(merchList.get(i).getPrice());
            ans.append(":\n\t");
            ans.append(merchList.get(i).getDescription());
            ans.append(", ");
        }
        if (!merchList.isEmpty()) {
            ans.append(merchList.get(i).getName());
            ans.append(" - ");
            ans.append(merchList.get(i).getPrice());
            ans.append(":\n\t");
            ans.append(merchList.get(i).getDescription());
        }
        ans.append("]");

        ans.append("\n\nEvents:\n\t[");
        ArrayList<Artist.Event> events = ((Artist) artistPage.getOwner()).getEvents();
        for (i = 0; i + 1 < events.size(); i++) {
            ans.append(events.get(i).getName());
            ans.append(" - ");
            ans.append(events.get(i).getDate());
            ans.append(":\n\t");
            ans.append(events.get(i).getDescription());
            ans.append(", ");
        }
        if (!events.isEmpty()) {
            ans.append(events.get(i).getName());
            ans.append(" - ");
            ans.append(events.get(i).getDate());
            ans.append(":\n\t");
            ans.append(events.get(i).getDescription());
        }
        ans.append("]");
        return ans.toString();
    }

    @Override
    public String visitPage(final HostPage hostPage) {
        StringBuilder ans = new StringBuilder("Podcasts:\n\t[");
        int i;
        ArrayList<PodcastInput> podcasts = ((Host) hostPage.getOwner()).getPodcasts();
        for (i = 0; i + 1 < podcasts.size(); i++) {
            ans.append(podcasts.get(i).getName() + ":\n\t[");
            int j;
            for (j = 0; j + 1 < podcasts.get(i).getEpisodes().size(); j++) {
                ans.append(podcasts.get(i).getEpisodes().get(j).getName());
                ans.append(" - ");
                ans.append(podcasts.get(i).getEpisodes().get(j).getDescription());
                ans.append(", ");
            }
            ans.append(podcasts.get(i).getEpisodes().get(j).getName());
            ans.append(" - ");
            ans.append(podcasts.get(i).getEpisodes().get(j).getDescription());
            ans.append("]");
            ans.append("\n, ");
        }
        ans.append(podcasts.get(i).getName() + ":\n\t[");
        int j;
        for (j = 0; j + 1 < podcasts.get(i).getEpisodes().size(); j++) {
            ans.append(podcasts.get(i).getEpisodes().get(j).getName());
            ans.append(" - ");
            ans.append(podcasts.get(i).getEpisodes().get(j).getDescription());
            ans.append(", ");
        }
        ans.append(podcasts.get(i).getEpisodes().get(j).getName());
        ans.append(" - ");
        ans.append(podcasts.get(i).getEpisodes().get(j).getDescription());
        ans.append("]");
        ans.append("\n]");

        ans.append("\n\nAnnouncements:\n\t[");
        ArrayList<Host.Announcement> announcements =
                ((Host) hostPage.getOwner()).getAnnouncements();
        for (i = 0; i + 1 < announcements.size(); i++) {
            ans.append(announcements.get(i).getName());
            ans.append(":\n\t");
            ans.append(announcements.get(i).getDescription());
            ans.append(", ");
        }
        if (!announcements.isEmpty()) {
            ans.append(announcements.get(i).getName());
            ans.append(":\n\t");
            ans.append(announcements.get(i).getDescription());
        }
        ans.append("]");

        return ans.toString();
    }
}
