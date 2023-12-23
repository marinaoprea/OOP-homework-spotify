package pages.pageNavigation;

import fileio.input.SongInput;
import main.Database;
import main.user.Artist;
import main.user.User;

public class ChangeArtist implements ChangePage {
    @Override
    public String execute(Database database, User user) {
        if (user.getSelectedType().equals("host") || user.getSelectedType().equals("podcast")
                || user.getSelectedType().equals("playlist")) {
            return user.getUsername() + " is trying to access a non-existent page.";
        }

        if (user.getSelectedType().equals("song")) {
            SongInput song = user.getSongFromUser(database);
            if (song == null) {
                return user.getUsername() + " is trying to access a non-existent page.";
            }
            Artist artist = database.findArtist(song.getArtist());
            if (artist == null) {
                return user.getUsername() + " is trying to access a non-existent page.";
            }
            user.setCurrentPage(artist.getArtistPage());
            return user.getUsername() + " accessed Artist successfully.";
        }

        if (user.getSelectedType().equals("album")) {
            SongInput song = user.getSongFromUserInAlbum(database);
            if (song == null) {
                return user.getUsername() + " is trying to access a non-existent page.";
            }
            Artist artist = database.findArtist(song.getArtist());
            if (artist == null) {
                return user.getUsername() + " is trying to access a non-existent page.";
            }
            user.setCurrentPage(artist.getArtistPage());
            return user.getUsername() + " accessed Artist successfully.";
        }

        if (user.getSelectedType().equals("artist")) {
            return user.getUsername() + " accessed Artist successfully.";
        }

        return user.getUsername() + " is trying to access a non-existent page.";
    }
}
