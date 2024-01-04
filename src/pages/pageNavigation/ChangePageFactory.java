package pages.pageNavigation;

public final class ChangePageFactory {

    /**
     * method returns new Change Page object depending on requested type
     * @param type type of Change Page command
     * @return new Change Page command
     */
    public static ChangePage getChangePage(final String type) {
        return switch (type) {
            case "Home" -> new ChangeHome();
            case "LikedContent" -> new ChangeLikedContent();
            case "Artist" -> new ChangeArtist();
            case "Host" -> new ChangeHost();
            default -> new ChangeHome();
        };
    }
}
