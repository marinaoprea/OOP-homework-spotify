package pages.pageNavigation;

import fileio.input.SongInput;
import main.Database;
import main.user.Artist;
import main.user.User;

public final class ChangeArtist implements ChangePage {

    /**
     * method returns corresponding messages;
     * method gets listened artist and changes user's page to artist's page
     * @param database extended input library
     * @param user user to change page
     * @return completion message
     */
    @Override
    public String execute(final Database database, final User user) {
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
