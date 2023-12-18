package pages;

import main.user.User;

public final class ArtistPage extends Page implements VisitablePage {
    public ArtistPage(final User owner) {
        super(owner);
    }

    @Override
    public String accept(final PageVisitor pageVisitor) {
        return pageVisitor.visitPage(this);
    }
}
