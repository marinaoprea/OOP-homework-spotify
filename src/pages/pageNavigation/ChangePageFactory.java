package pages.pageNavigation;

public class ChangePageFactory {
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
