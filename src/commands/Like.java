package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;
import main.CommandInput;
import main.Database;
import main.Album;
import main.Playlist;
import main.user.User;

public final class Like extends Command {
    private String message;
    public Like(final CommandInput commandInput) {
        super(commandInput);
    }

    /**
     * method sets corresponding error messages;
     * simulation is performed up until this moment;
     * method checks if user has already liked song; if so, unlike is registered,
     * otherwise like is registered;
     * song's number of likes in incremented/decremented;
     * song is added/removed from user's favourites playlist;
     * song is considered even if loaded audio file is a playlist; we check current
     * song;
     * @param database extended input library
     */
    @Override
    public void execute(final Database database) {
        User user = database.findUserInDatabase(this.getUsername());
        if (user == null) {
            return;
        }
        if (!user.isConnectionStatus()) {
            this.message = user.getUsername() + " is offline.";
            return;
        }
        if (user.getTimeLoaded() == 0) {
            this.message = "Please load a source before liking or unliking.";
            return;
        }
        user.simulate(this.getTimestamp(), database);
        if (user.getSelectedType().equals("podcast")) {
            this.message = "Loaded source is not a song.";
            return;
        }
        SongInput song = null;
        if (user.getSelectedType().equals("song")) {
            song = user.getSongFromUser(database);
        } else { // for playlist
            if (user.getSelectedType().equals("playlist")) {
                if (user.getSelectedIndex() < 1
                        || user.getSelectedIndex() > user.getLastSearch().getResults().size()) {
                    this.message = "Please load a source before liking or unliking.";
                    return;
                }

                String playlistName =
                        user.getLastSearch().getResults().get(user.getSelectedIndex() - 1);
                Playlist playlist = user.findPlaylist(playlistName);
                if (playlist == null) {
                    return;
                }
                if (user.getSelectedIndexInList() > 0
                        && user.getSelectedIndexInList() <= playlist.getSongs().size()) {
                    song = playlist.getSongs().get(user.getSelectedIndexInList() - 1);
                }
            } else {
                if (user.getSelectedType().equals("album")) {
                    String albumName = user.getLoadedSourceName();
                    if (albumName.isEmpty()) {
                        this.message = "Please load a source before liking or unliking.";
                        return;
                    }

                    Album album = database.findAlbum(albumName);
                    if (album == null) {
                        return;
                    }
                    if (user.getSelectedIndexInList() > 0
                            && user.getSelectedIndexInList() <= album.getSongs().size()) {
                        song = album.getSongs().get(user.getSelectedIndexInList() - 1);
                    }
                }
            }
        }
        if (song == null) {
            this.message = "Please load a source before liking or unliking.";
            return;
        }
        if (!user.getFavourites().findSong(song)) {
            user.getFavourites().addSong(song);
            database.incrementLikes(database.getSongs().indexOf(song));
            song.setNoLikes(song.getNoLikes() + 1);
            this.message = "Like registered successfully.";
            return;
        }
        user.getFavourites().removeSong(song);
        database.decrementLikes(database.getSongs().indexOf(song));
        song.setNoLikes(song.getNoLikes() - 1);
        this.message = "Unlike registered successfully.";
    }

    @Override
    public void convertToObjectNode(final ObjectNode objectNode) {
        super.convertToObjectNode(objectNode);
        objectNode.put("message", this.message);
    }
}
