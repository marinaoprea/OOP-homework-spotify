package pages;

import lombok.Getter;
import main.user.User;
public class Page implements VisitablePage {
    @Getter
    private User owner;

    /**
     * constructor
     * @param owner page owner
     */
    public Page(final User owner) {
        this.owner = owner;
    }

    /**
     * method accepts page visitor
     * @param pageVisitor visitor to be accepted
     * @return string produced by visitor
     */
    @Override
    public String accept(final PageVisitor pageVisitor) {
        return pageVisitor.visitPage(this);
    }
}
