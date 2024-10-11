package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents an assignees widget
 */
@Name("WorkItemWidgetAssignees")
public class WorkItemWidgetAssignees implements WorkItemWidget {

    /**
     * Assignees of the work item.
     */
    private UserCoreConnection assignees;
    /**
     * Widget type.
     */
    private WorkItemWidgetType type;

    public UserCoreConnection getAssignees() {
        return assignees;
    }

    public void setAssignees(UserCoreConnection assignees) {
        this.assignees = assignees;
    }

    public WorkItemWidgetType getType() {
        return type;
    }

    public void setType(WorkItemWidgetType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignees, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetAssignees other = (WorkItemWidgetAssignees) obj;
        return Objects.equals(assignees, other.assignees) && Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetAssignees [assignees=" + assignees + ", type=" + type + "]";
    }

}
