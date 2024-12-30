package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Information about pagination in a connection.
 */
@Name("PageInfo")
public class PageInfo {

    /**
     * When paginating forwards, the cursor to continue.
     */
    private String endCursor;

    /**
     * When paginating forwards, are there more items?
     */
    private Boolean hasNextPage;

    /**
     * When paginating backwards, the cursor to continue.
     */
    private String startCursor;

    public String getEndCursor() {
        return endCursor;
    }

    public PageInfo setEndCursor(String endCursor) {
        this.endCursor = endCursor;
        return this;
    }

    public Boolean getHasNextPage() {
        return hasNextPage;
    }

    public PageInfo setHasNextPage(Boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
        return this;
    }

    public String getStartCursor() {
        return startCursor;
    }

    public PageInfo setStartCursor(String startCursor) {
        this.startCursor = startCursor;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(endCursor, hasNextPage, startCursor);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PageInfo other = (PageInfo) obj;
        return Objects.equals(endCursor, other.endCursor) && Objects.equals(hasNextPage, other.hasNextPage) && Objects.equals(startCursor, other.startCursor);
    }

    @Override
    public String toString() {
        return "PageInfo [endCursor=" + endCursor + ", hasNextPage=" + hasNextPage + ", startCursor=" + startCursor + "]";
    }

}
