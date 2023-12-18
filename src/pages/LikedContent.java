package pages;

import main.user.User;

public final class LikedContent extends Page implements VisitablePage {
    public LikedContent(final User owner) {
        super(owner);
    }
    @Override
    public String accept(final PageVisitor pageVisitor) {
        return pageVisitor.visitPage(this);
    }
}
