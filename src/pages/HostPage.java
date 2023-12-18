package pages;

import main.user.User;

public final class HostPage extends Page implements VisitablePage {
    public HostPage(final User owner) {
        super(owner);
    }
    @Override
    public String accept(final PageVisitor pageVisitor) {
        return pageVisitor.visitPage(this);
    }
}
