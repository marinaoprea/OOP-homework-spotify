package main;

public interface Recommendation {
    /**
     * method returns recommendation name
     */
    String getRecommendationName();

    /**
     * method returns recommendation type ("song", "playlist")
     */
    String getType();
}
