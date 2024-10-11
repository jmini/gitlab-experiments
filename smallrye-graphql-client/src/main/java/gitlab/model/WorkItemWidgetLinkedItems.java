package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents the linked items widget
 */
@Name("WorkItemWidgetLinkedItems")
public class WorkItemWidgetLinkedItems implements WorkItemWidget {

    /**
     * Linked items for the work item. Introduced in GitLab 16.3: **Status**: Experiment.
     * @deprecated **Status**: Experiment. Introduced in GitLab 16.3.
     */
    @Deprecated
    private LinkedWorkItemTypeConnection linkedItems;
    /**
     * Widget type.
     */
    private WorkItemWidgetType type;

    @Deprecated
    public LinkedWorkItemTypeConnection getLinkedItems() {
        return linkedItems;
    }

    @Deprecated
    public void setLinkedItems(LinkedWorkItemTypeConnection linkedItems) {
        this.linkedItems = linkedItems;
    }

    public WorkItemWidgetType getType() {
        return type;
    }

    public void setType(WorkItemWidgetType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkedItems, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetLinkedItems other = (WorkItemWidgetLinkedItems) obj;
        return Objects.equals(linkedItems, other.linkedItems) && Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetLinkedItems [linkedItems=" + linkedItems + ", type=" + type + "]";
    }

}
