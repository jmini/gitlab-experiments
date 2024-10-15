package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("LinkedWorkItemType")
public class LinkedWorkItemType {

    /**
     * Timestamp the link was created.
     */
    private String linkCreatedAt;
    /**
     * Global ID of the link.
     */
    private String linkId;
    /**
     * Type of link.
     */
    private String linkType;
    /**
     * Timestamp the link was updated.
     */
    private String linkUpdatedAt;
    /**
     * Linked work item.
     */
    private WorkItemRef workItem;

    public String getLinkCreatedAt() {
        return linkCreatedAt;
    }

    public void setLinkCreatedAt(String linkCreatedAt) {
        this.linkCreatedAt = linkCreatedAt;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getLinkUpdatedAt() {
        return linkUpdatedAt;
    }

    public void setLinkUpdatedAt(String linkUpdatedAt) {
        this.linkUpdatedAt = linkUpdatedAt;
    }

    public WorkItemRef getWorkItem() {
        return workItem;
    }

    public void setWorkItem(WorkItemRef workItem) {
        this.workItem = workItem;
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkCreatedAt, linkId, linkType, linkUpdatedAt, workItem);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LinkedWorkItemType other = (LinkedWorkItemType) obj;
        return Objects.equals(linkCreatedAt, other.linkCreatedAt) && Objects.equals(linkId, other.linkId) && Objects.equals(linkType, other.linkType) && Objects.equals(linkUpdatedAt, other.linkUpdatedAt) && Objects.equals(workItem, other.workItem);
    }

    @Override
    public String toString() {
        return "LinkedWorkItemType [linkCreatedAt=" + linkCreatedAt + ", linkId=" + linkId + ", linkType=" + linkType + ", linkUpdatedAt=" + linkUpdatedAt + ", workItem=" + workItem + "]";
    }

}
