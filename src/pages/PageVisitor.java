package pages;

public interface PageVisitor {
    /**
     * method visits generic page
     * @param page page to be visited
     * @return constructed string by visitor
     */
    String visitPage(Page page);

    /**
     * method visits home page
     * @param homePage page to be visited
     * @return constructed string by visitor
     */
    String visitPage(HomePage homePage);

    /**
     * method visits liked content page
     * @param likedContent page to be visited
     * @return constructed string by visitor
     */
    String visitPage(LikedContent likedContent);

    /**
     * method visits artist page
     * @param artistPage page to be visited
     * @return constructed string by visitor
     */
    String visitPage(ArtistPage artistPage);

    /**
     * method visits host page
     * @param hostPage page to be visited
     * @return constructed string by visitor
     */
    String visitPage(HostPage hostPage);
}
