package main;

import fileio.input.SongInput;
import lombok.Getter;
import main.user.Artist;
import main.user.User;

import java.util.ArrayList;

public final class Album implements Wrappeable{
    @Getter
    private final String name;
    private final int releaseYear;
    @Getter
    private final String description;
    @Getter
    private final Artist owner;
    @Getter
    private final ArrayList<SongInput> songs;

    @Getter
    private int listens;

    public Album(final String name, final int releaseYear, final String description,
                 final Artist owner, final ArrayList<SongInput> songs) {
        this.songs = songs;
        this.name = name;
        this.releaseYear = releaseYear;
        this.description = description;
        this.owner = owner;
    }

    @Override
    public String extractName() {
        return this.name;
    }

    /**
     * method checks if album contains specific song given by name
     * @param songName name of searched song
     * @return true if song is contained; false otherwise
     */
    public boolean containsSongName(final String songName) {
        for (SongInput song: songs) {
            if (song.getName().equals(songName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * method checks if album could be deleted;
     * method checks if any user is listening to the album;
     * method checks if any user is listening to any song of the album;
     * method checks if any user is listening to a playlist which contains any song of the album
     * @param timestamp current timestamp used to update user's current status to this moment
     * @param database extended input library
     * @return true if album could be deleted; false otherwise
     */
    public boolean checkDelete(final int timestamp, final Database database) {
        for (User user: database.getUsers()) {
            user.simulate(timestamp, database);
            String source = user.getLoadedSourceName();
            if (user.getSelectedType().equals("album") && user.getTimeLoaded() != 0
                    && source.equals(this.getName())) {
                return false;
            }
            if (user.getSelectedType().equals("song") && user.getTimeLoaded() != 0
                    && this.containsSongName(source)) {
                return false;
            }
            if (user.getSelectedType().equals("playlist")) {
                Playlist playlist = database.findPlaylistInDatabase(source);
                if (playlist == null || user.getTimeLoaded() == 0) {
                    continue;
                }
                for (SongInput song: this.songs) {
                    if (playlist.getSongs().contains(song)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * method clears album songs;
     * method clears songs from database;
     * method clears song from any playlist;
     * method updates a user's selected index in playlist if song is removed from playlist
     * @param database extended input library
     */
    public void clearSongs(final Database database) {
        for (SongInput song : this.songs) {
            for (Playlist playlist : database.getGlobalPlaylists()) {
                if (playlist.getSongs().contains(song)) {
                    int index = playlist.getSongs().indexOf(song);
                    for (User user : database.getUsers()) {
                        if (user.getLoadedSourceName().equals(playlist.getName())
                                && user.getTimeLoaded() > 0
                                && user.getSelectedIndexInList() - 1 > index) {
                            user.setSelectedIndexInList(user.getSelectedIndexInList() - 1);
                        }
                    }
                    playlist.getSongs().remove(song);
                }
            }
            for (User user : database.getUsers()) {
                user.getFavourites().getSongs().remove(song);
            }
            database.getNoLikesPerSong().remove(database.getSongs().indexOf(song));
            database.getSongs().remove(song);
        }
    }

    /**
     * method calculates total number of likes of album
     * @return total number of likes of album
     */
    public int getNoLikes() {
        int ans = 0;
        for (SongInput song: this.songs) {
            ans += song.getNoLikes();
        }
        return ans;
    }

    public void setListens(int listens) {
        this.listens = listens;
    }
}
