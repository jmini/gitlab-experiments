package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("LinkedWorkItemType")
public class LinkedWorkItemType {

    /**
     * Timestamp the link was created.
     */
    private Time linkCreatedAt;
    /**
     * Global ID of the link.
     */
    private WorkItemsRelatedWorkItemLinkID linkId;
    /**
     * Type of link.
     */
    private String linkType;
    /**
     * Timestamp the link was updated.
     */
    private Time linkUpdatedAt;
    /**
     * Linked work item.
     */
    private WorkItemRef workItem;

    public Time getLinkCreatedAt() {
        return linkCreatedAt;
    }

    public LinkedWorkItemType setLinkCreatedAt(Time linkCreatedAt) {
        this.linkCreatedAt = linkCreatedAt;
        return this;
    }

    public WorkItemsRelatedWorkItemLinkID getLinkId() {
        return linkId;
    }

    public LinkedWorkItemType setLinkId(WorkItemsRelatedWorkItemLinkID linkId) {
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

    public Time getLinkUpdatedAt() {
        return linkUpdatedAt;
    }

    public LinkedWorkItemType setLinkUpdatedAt(Time linkUpdatedAt) {
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
