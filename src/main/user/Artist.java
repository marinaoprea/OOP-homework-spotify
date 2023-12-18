package main.user;

import fileio.input.SongInput;
import lombok.Getter;
import main.Album;
import main.Database;
import main.Playlist;
import pages.ArtistPage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Artist extends User {
    @Getter
    private final ArtistPage artistPage = new ArtistPage(this);

    @Getter
    private final ArrayList<Album> albums = new ArrayList<Album>();

    /**
     * constructor
     * @param username username of new artist
     * @param age age of new artist
     * @param city city of new artist
     */
    public Artist(final String username, final int age, final String city) {
        super(username, age, city);
    }

    /**
     * method checks if artist contains specific album given by name in his album list
     * @param albumName name of searched album
     * @return true if album exists in list; false otherwise
     */
    public boolean checkAlbumByName(final String albumName) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * method searches specific album given by name in artist's album list
     * @param albumName name of searched album
     * @return found album if existent; null otherwise
     */
    public Album getAlbumByName(final String albumName) {
        for (Album album: albums) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }
        return null;
    }

    /**
     * inner class that serves event type, specific to artist
     */
    public static class Event {
        @Getter
        private String name;
        @Getter
        private String date;
        @Getter
        private String description;

        /**
         * default constructor
         */
        public Event() {

        }

        /**
         * constructor
         * @param name name of new event
         * @param date date of new event
         * @param description description of new event
         */
        public Event(final String name, final String date, final String description) {
            this.date = date;
            this.description = description;
            this.name = name;
        }

        /**
         * method checks if event date is valid
         * @return true if valid; false otherwise
         */
        public boolean checkDate() {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            df.setLenient(false);
            try {
                Date date1 = df.parse(this.date);
                if (date1.before(df.parse("01-01-1900")) || date1.after(df.parse("01-01-2023"))) {
                    return false;
                }
            } catch (ParseException parseException) {
                return false;
            }
            return true;
        }
    }

    @Getter
    private final ArrayList<Event> events = new ArrayList<Event>();

    /**
     * inner class that serves event type, specific to artist
     */
    public static class Merch {
        @Getter
        private int price;
        @Getter
        private String name;
        @Getter
        private String description;

        /**
         * default constructor
         */
        public Merch() {

        }

        /**
         * constructor
         * @param name name of new merch
         * @param description description of new merch
         * @param price price of new merch
         */
        public Merch(final String name, final String description, final int price) {
            this.name = name;
            this.description = description;
            this.price = price;
        }
    }

    @Getter
    private final ArrayList<Merch> artistMerchList = new ArrayList<Merch>();

    /**
     * method checks artist contains specific event name in his event list
     * @param eventName name of searched event
     * @return true if event found; false otherwise
     */
    public boolean findEventByName(final String eventName) {
        for (Event event : events) {
            if (event.name.equals(eventName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * method checks if artist contains specific merch in his merch list
     * @param merchName name of searched merch
     * @return true if merch found; false otherwise
     */
    public boolean findMerchByName(final String merchName) {
        for (Merch merch : artistMerchList) {
            if (merch.name.equals(merchName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * method searches specific event by name in artist's event list
     * @param eventName name of searched event
     * @return found event if existent; null otherwise
     */
    public Event getEvent(final String eventName) {
        for (Event event: events) {
            if (event.name.equals(eventName)) {
                return event;
            }
        }
        return null;
    }

    /**
     * method checks if artist could be deleted; firstly, it checks if any other user listens to
     * any playlist created by artist;
     * then it checks if artist's albums could be deleted;
     * then it checks if any user is currently on artist's page
     * @param timestamp timestamp of check, used to update users' status to this moment
     * @param database extended input library
     * @return true if host could be deleted; false otherwise
     */
    @Override
    public boolean check(final int timestamp, final Database database) {
        if (!super.check(timestamp, database)) {
            return false;
        }
        for (Album album: albums) {
            if (!album.checkDelete(timestamp, database)) {
                return false;
            }
        }
        for (User user: database.getUsers()) {
            if (user.getCurrentPage() == this.artistPage) {
                return false;
            }
        }
        return true;
    }

    /**
     * method clears artist's contribution to the global database;
     * method clears all playlists created by artist;
     * then method clears all albums created by artist and all songs introduced by artist;
     * songs are deleted from global database, from all playlists that contains them and
     * from any user's favourites playlist
     * @param database extended input library
     */
    @Override
    public void clear(final Database database) {
        super.clear(database);
        for (Album album : albums) {
            for (SongInput song : album.getSongs()) {
                for (Playlist playlist : database.getGlobalPlaylists()) {
                    playlist.getSongs().remove(song);
                }
                for (User user : database.getUsers()) {
                    user.getFavourites().removeSong(song);
                }
                database.getNoLikesPerSong().remove(database.getSongs().indexOf(song));
                database.getSongs().remove(song);
            }
            database.getAlbums().remove(album);
        }
    }

    /**
     * method calculates an artist's total number of likes
     * @return total number of likes of an artist
     */
    public int getNoLikes() {
        int ans = 0;
        for (Album album: albums) {
            ans += album.getNoLikes();
        }
        return ans;
    }
}
