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

    public LinkedWorkItemType setLinkCreatedAt(String linkCreatedAt) {
        this.linkCreatedAt = linkCreatedAt;
        return this;
    }

    public String getLinkId() {
        return linkId;
    }

    public LinkedWorkItemType setLinkId(String linkId) {
        this.linkId = linkId;
        return this;
    }

    public String getLinkType() {
        return linkType;
    }

    public LinkedWorkItemType setLinkType(String linkType) {
        this.linkType = linkType;
        return this;
    }

    public String getLinkUpdatedAt() {
        return linkUpdatedAt;
    }

    public LinkedWorkItemType setLinkUpdatedAt(String linkUpdatedAt) {
        this.linkUpdatedAt = linkUpdatedAt;
        return this;
    }

    public WorkItemRef getWorkItem() {
        return workItem;
    }

    public LinkedWorkItemType setWorkItem(WorkItemRef workItem) {
        this.workItem = workItem;
        return this;
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
