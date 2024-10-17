package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("WorkItemWidgetHierarchyUpdateInput")
public class WorkItemWidgetHierarchyUpdateInput {

    /**
     * ID of the work item to be switched with.
     */
    private WorkItemID adjacentWorkItemId;
    /**
     * Global IDs of children work items.
     */
    private List<WorkItemID> childrenIds;
    /**
     * Global ID of the parent work item. Use `null` to remove the association.
     */
    private WorkItemID parentId;
    /**
     * Type of switch. Valid values are `BEFORE` or `AFTER`.
     */
    private RelativePositionType relativePosition;

    public WorkItemID getAdjacentWorkItemId() {
        return adjacentWorkItemId;
    }

    public WorkItemWidgetHierarchyUpdateInput setAdjacentWorkItemId(WorkItemID adjacentWorkItemId) {
        this.adjacentWorkItemId = adjacentWorkItemId;
        return this;
    }

    public List<WorkItemID> getChildrenIds() {
        return childrenIds;
    }

    public WorkItemWidgetHierarchyUpdateInput setChildrenIds(List<WorkItemID> childrenIds) {
        this.childrenIds = childrenIds;
        return this;
    }

    public WorkItemID getParentId() {
        return parentId;
    }

    public WorkItemWidgetHierarchyUpdateInput setParentId(WorkItemID parentId) {
        this.parentId = parentId;
        return this;
    }

    public RelativePositionType getRelativePosition() {
        return relativePosition;
    }

    public WorkItemWidgetHierarchyUpdateInput setRelativePosition(RelativePositionType relativePosition) {
        this.relativePosition = relativePosition;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(adjacentWorkItemId, childrenIds, parentId, relativePosition);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetHierarchyUpdateInput other = (WorkItemWidgetHierarchyUpdateInput) obj;
        return Objects.equals(adjacentWorkItemId, other.adjacentWorkItemId) && Objects.equals(childrenIds, other.childrenIds) && Objects.equals(parentId, other.parentId) && Objects.equals(relativePosition, other.relativePosition);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetHierarchyUpdateInput [adjacentWorkItemId=" + adjacentWorkItemId + ", childrenIds=" + childrenIds + ", parentId=" + parentId + ", relativePosition=" + relativePosition + "]";
    }

}
