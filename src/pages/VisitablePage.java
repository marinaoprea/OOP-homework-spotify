package pages;

public interface VisitablePage {
    /**
     * method accepts visitor
     * @param pageVisitor visitor to be accepted
     * @return string constructed by visitor
     */
    String accept(PageVisitor pageVisitor);
}
