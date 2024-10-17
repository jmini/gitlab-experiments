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
    private String dueDate;
    /**
     * Indicates if the work item is using fixed dates.
     */
    private Boolean isFixed;
    /**
     * Indicates if the work item can use rolled up dates.
     */
    private Boolean rollUp;
    /**
     * Start date of the work item.
     */
    private String startDate;
    /**
     * Widget type.
     */
    private WorkItemWidgetType type;

    public String getDueDate() {
        return dueDate;
    }

    public WorkItemWidgetStartAndDueDate setDueDate(String dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public Boolean getIsFixed() {
        return isFixed;
    }

    public WorkItemWidgetStartAndDueDate setIsFixed(Boolean isFixed) {
        this.isFixed = isFixed;
        return this;
    }

    public Boolean getRollUp() {
        return rollUp;
    }

    public WorkItemWidgetStartAndDueDate setRollUp(Boolean rollUp) {
        this.rollUp = rollUp;
        return this;
    }

    public String getStartDate() {
        return startDate;
    }

    public WorkItemWidgetStartAndDueDate setStartDate(String startDate) {
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
        return Objects.hash(dueDate, isFixed, rollUp, startDate, type);
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
        return Objects.equals(dueDate, other.dueDate) && Objects.equals(isFixed, other.isFixed) && Objects.equals(rollUp, other.rollUp) && Objects.equals(startDate, other.startDate) && Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetStartAndDueDate [dueDate=" + dueDate + ", isFixed=" + isFixed + ", rollUp=" + rollUp + ", startDate=" + startDate + ", type=" + type + "]";
    }

}
