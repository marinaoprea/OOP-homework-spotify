package fileio.input;

import lombok.Getter;
import main.Recommendation;
import main.Wrappeable;

import java.util.ArrayList;
import java.util.Objects;

public final class SongInput implements Wrappeable, Recommendation {
    private String name;
    private Integer duration;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;
    private int noLikes;

    @Getter
    private int id;

    /**
     * overriden equals method for hashmaps used in wrapped command;
     * only song name and artist are taken into consideration
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SongInput songInput = (SongInput) o;

        if (!name.equals(songInput.name)) return false;
        return artist.equals(songInput.artist);
    }

    /**
     * overriden hashcode method overrode for wrapped hashmaps;
     * only name and artist are taken into consideration
     */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + artist.hashCode();
        return result;
    }

    /**
     * method implemented for Recommendation interface
     * @return name of recommendation
     */
    @Override
    public String getRecommendationName() {
        return this.name;
    }

    /**
     * method implemented for Recommendation interface
     * @return type of recommendation
     */
    @Override
    public String getType() {
        return "song";
    }

    /**
     * method implemented for Wrappeable interface
     * @return name of the Wrappeable
     */
    @Override
    public String extractName() {
        return this.name;
    }

    public int getNoLikes() {
        return noLikes;
    }

    public void setNoLikes(final int noLikes) {
        this.noLikes = noLikes;
    }

    public SongInput() {
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(final ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(final String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public void setId(int id) {
        this.id = id;
    }
}
