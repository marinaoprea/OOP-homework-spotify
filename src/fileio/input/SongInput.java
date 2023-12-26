package fileio.input;

import lombok.Getter;
import main.Wrappeable;

import java.util.ArrayList;
import java.util.Objects;

public final class SongInput implements Wrappeable {
    private String name;
    private Integer duration;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;
    private int noLikes;
    private int listens;

    @Getter
    private int id;

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SongInput songInput = (SongInput) o;

        if (noLikes != songInput.noLikes) return false;
        if (listens != songInput.listens) return false;
        if (id != songInput.id) return false;
        if (!name.equals(songInput.name)) return false;
        if (!duration.equals(songInput.duration)) return false;
        if (!album.equals(songInput.album)) return false;
        if (!tags.equals(songInput.tags)) return false;
        if (!lyrics.equals(songInput.lyrics)) return false;
        if (!genre.equals(songInput.genre)) return false;
        if (!releaseYear.equals(songInput.releaseYear)) return false;
        return artist.equals(songInput.artist);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + duration.hashCode();
        result = 31 * result + album.hashCode();
        result = 31 * result + tags.hashCode();
        result = 31 * result + lyrics.hashCode();
        result = 31 * result + genre.hashCode();
        result = 31 * result + releaseYear.hashCode();
        result = 31 * result + artist.hashCode();
        result = 31 * result + noLikes;
        result = 31 * result + listens;
        result = 31 * result + id;
        return result;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SongInput songInput = (SongInput) o;

        return name.equals(songInput.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String extractName() {
        return this.name;
    }

    public int getListens() {
        return listens;
    }

    public void setListens(final int listens) {
        this.listens = listens;
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
