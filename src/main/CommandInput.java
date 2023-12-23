package main;

import fileio.input.EpisodeInput;
import fileio.input.SongInput;

import java.util.ArrayList;

/**
 * class that used for parsing command input as json;
 * class reunites all possible fields of given commands
 */

public final class CommandInput {
    private String command;
    private String username;
    private int timestamp;
    private String type;
    private Filter filters;
    private int itemNumber;
    private int seed;
    private int playlistId;
    private String playlistName;
    private String nextPage;
    private ArrayList<SongInput> songs;
    private String description;
    private String date;
    private int price;
    private ArrayList<EpisodeInput> episodes;
    private int age;
    private String city;
    private String name;
    private int releaseYear;
    private String recommendationType;

    public CommandInput() {

    }
    public CommandInput(final String commandType, final int timestamp) {
        this.command = commandType;
        this.timestamp = timestamp;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(final String recommendationType) {
        this.recommendationType = recommendationType;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public ArrayList<SongInput> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(final String nextPage) {
        this.nextPage = nextPage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public ArrayList<EpisodeInput> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(final ArrayList<EpisodeInput> episodes) {
        this.episodes = episodes;
    }

    /**
     * getter for playlist name
     * @return playlist name
     */
    public String getPlaylistName() {
        return playlistName;
    }

    /**
     * setter for playlist name
     * @param playlistName new playlist name
     */
    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    /**
     * getter for command name
     * @return command name
     */
    public String getCommand() {
        return command;
    }

    /**
     * setter for command name
     * @param command the name of the command
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * getter for username
     * @return the name of the user that gave the command
     */
    public String getUsername() {
        return username;
    }

    /**
     * setter for username
     * @param username new username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * getter for the timestamp at which command was given
     * @return timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * setter for timestamp at which command was given
     * @param timestamp new timestamp
     */
    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * getter for type of the audio file
     * @return string from {"song", "playlist", "podcast"}
     */
    public String getType() {
        return type;
    }

    /**
     * setter for type of the audio file
     * @param type new type set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * getter of filters for search command
     * @return search filters
     */
    public Filter getFilters() {
        return filters;
    }

    /**
     * setter for search filters
     * @param filters new search filters
     */
    public void setFilters(final Filter filters) {
        this.filters = filters;
    }

    /**
     * getter for index of selection
     * @return index of selection
     */
    public int getItemNumber() {
        return itemNumber;
    }

    /**
     * setter for index of selection
     * @param itemNumber new index of selection
     */
    public void setItemNumber(final int itemNumber) {
        this.itemNumber = itemNumber;
    }

    /**
     * getter for seed for shuffle command
     * @return seed
     */
    public int getSeed() {
        return seed;
    }

    /**
     * setter for seed of shuffle command
     * @param seed new seed set
     */
    public void setSeed(final int seed) {
        this.seed = seed;
    }

    /**
     * getter for playlist ID
     * @return playlist ID
     */
    public int getPlaylistId() {
        return playlistId;
    }

    /**
     * setter for playlist ID
     * @param playlistId new playlist ID set
     */
    public void setPlaylistId(final int playlistId) {
        this.playlistId = playlistId;
    }
}
