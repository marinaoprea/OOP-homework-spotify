package main;

import fileio.input.SongInput;
import main.user.User;

import java.util.ArrayList;

public final class Playlist implements Recommendation {
    private User user;
    private String name;
    private boolean visibility; // 0 - public; 1 - private
    private ArrayList<SongInput> songs = new ArrayList<SongInput>();
    private int followers;
    private int playlistid;
    private int timestamp;
    private int shuffleSeed;

    public Playlist(final User user) {
        this.user = user;
    }
    public Playlist() {

    }
    public Playlist(final User user, final String name, final boolean visibility) {
        this.user = user;
        this.name = name;
        this.visibility = visibility;
        this.playlistid = user.getPlaylistids() + 1;
        user.setPlaylistids(user.getPlaylistids() + 1);
    }

    @Override
    public String getRecommendationName() {
        return this.name;
    }

    @Override
    public String getType() {
        return "playlist";
    }

    /**
     * method calculates total number of likes pf playlist
     * @return total number of likes of playlist
     */
    public int getAllLikes() {
        int ans = 0;
        for (SongInput song : songs) {
            ans += song.getNoLikes();
        }
        return ans;
    }

    /**
     * method checks if playlist contains specific song;
     * @param song searched song
     * @return true if song is part of the playlist; false otherwise
     */
    public boolean findSong(final SongInput song) {
        return songs.contains(song);
    }

    /**
     * method adds song to playlist
     * @param song song to be added
     */
    public void addSong(final SongInput song) {
        songs.add(song);
    }

    /**
     * method removes song from playlist
     * @param song song to be removed
     */
    public void removeSong(final SongInput song) {
        songs.remove(song);
    }

    /**
     * method checks if playlist could be deleted;
     * that is if no user is listening to the playlist
     * @param timestampDelete current timestamp used to update status of users
     * @param database extended input library
     * @return true if playlist could be deleted; false otherwise
     */
    public boolean checkDelete(final int timestampDelete, final Database database) {
        for (User userIterator: database.getUsers()) {
            userIterator.simulate(timestampDelete, database);
            String source = userIterator.getLoadedSourceName();
            if (userIterator.getTimeLoaded() != 0 && source.equals(this.name)) {
                return false;
            }
        }
        return true;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    public int getShuffleSeed() {
        return shuffleSeed;
    }

    public void setShuffleSeed(final int shuffleSeed) {
        this.shuffleSeed = shuffleSeed;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(final int followers) {
        this.followers = followers;
    }

    public int getPlaylistid() {
        return playlistid;
    }

    public void setPlaylistid(final int playlistid) {
        this.playlistid = playlistid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(final boolean visibility) {
        this.visibility = visibility;
    }

    public ArrayList<SongInput> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }
}
