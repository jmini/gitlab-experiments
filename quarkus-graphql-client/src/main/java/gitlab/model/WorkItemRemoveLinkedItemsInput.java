package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Autogenerated input type of WorkItemRemoveLinkedItems
 */
@Name("WorkItemRemoveLinkedItemsInput")
public class WorkItemRemoveLinkedItemsInput {

    /**
     * Global ID of the work item.
     */
    private WorkItemID id;
    /**
     * Global IDs of the items to unlink. Maximum number of IDs you can provide: 10.
     */
    private List<WorkItemID> workItemsIds;

    public WorkItemID getId() {
        return id;
    }

    public WorkItemRemoveLinkedItemsInput setId(WorkItemID id) {
        this.id = id;
        return this;
    }

    public List<WorkItemID> getWorkItemsIds() {
        return workItemsIds;
    }

    public WorkItemRemoveLinkedItemsInput setWorkItemsIds(List<WorkItemID> workItemsIds) {
        this.workItemsIds = workItemsIds;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workItemsIds);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemRemoveLinkedItemsInput other = (WorkItemRemoveLinkedItemsInput) obj;
        return Objects.equals(id, other.id) && Objects.equals(workItemsIds, other.workItemsIds);
    }

    @Override
    public String toString() {
        return "WorkItemRemoveLinkedItemsInput [id=" + id + ", workItemsIds=" + workItemsIds + "]";
    }

}
