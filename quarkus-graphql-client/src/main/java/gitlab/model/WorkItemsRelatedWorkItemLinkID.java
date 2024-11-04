package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * A `WorkItemsRelatedWorkItemLinkID` is a global ID. It is encoded as a string.
 *
 * An example `WorkItemsRelatedWorkItemLinkID` is: `"gid://gitlab/WorkItems::RelatedWorkItemLink/1"`.
 */
@Name("WorkItemsRelatedWorkItemLinkID")
public class WorkItemsRelatedWorkItemLinkID {

    private String value;

    public WorkItemsRelatedWorkItemLinkID(String value) {
        this.value = value;
    }

    public static WorkItemsRelatedWorkItemLinkID valueOf(String value) {
        return new WorkItemsRelatedWorkItemLinkID(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemsRelatedWorkItemLinkID other = (WorkItemsRelatedWorkItemLinkID) obj;
        return Objects.equals(value, other.value);
    }
}
