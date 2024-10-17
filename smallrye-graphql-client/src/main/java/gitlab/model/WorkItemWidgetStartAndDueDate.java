package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents a start and due date widget
 */
@Name("WorkItemWidgetStartAndDueDate")
public class WorkItemWidgetStartAndDueDate implements WorkItemWidget {

    /**
     * Due date of the work item.
     */
    private Date dueDate;
    /**
     * Start date of the work item.
     */
    private Date startDate;
    /**
     * Widget type.
     */
    private WorkItemWidgetType type;

    public Date getDueDate() {
        return dueDate;
    }

    public WorkItemWidgetStartAndDueDate setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public WorkItemWidgetStartAndDueDate setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public WorkItemWidgetType getType() {
        return type;
    }

    public WorkItemWidgetStartAndDueDate setType(WorkItemWidgetType type) {
        this.type = type;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dueDate, startDate, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetStartAndDueDate other = (WorkItemWidgetStartAndDueDate) obj;
        return Objects.equals(dueDate, other.dueDate) && Objects.equals(startDate, other.startDate) && Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetStartAndDueDate [dueDate=" + dueDate + ", startDate=" + startDate + ", type=" + type + "]";
    }

}
