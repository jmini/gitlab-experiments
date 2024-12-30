package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

import jakarta.json.bind.annotation.JsonbProperty;

@Name("WorkItemWidgetStartAndDueDateUpdateInput")
public class WorkItemWidgetStartAndDueDateUpdateInput {

    /**
     * Due date for the work item.
     */
    @JsonbProperty(value = "dueDate", nillable = true)
    private Date dueDate;

    /**
     * Start date for the work item.
     */
    @JsonbProperty(value = "startDate", nillable = true)
    private Date startDate;

    /**
     * Indicates if the work item is using fixed dates.
     */
    private Boolean isFixed;

    public Date getDueDate() {
        return dueDate;
    }

    public WorkItemWidgetStartAndDueDateUpdateInput setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public WorkItemWidgetStartAndDueDateUpdateInput setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Boolean getIsFixed() {
        return isFixed;
    }

    public WorkItemWidgetStartAndDueDateUpdateInput setIsFixed(Boolean isFixed) {
        this.isFixed = isFixed;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dueDate, startDate, isFixed);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetStartAndDueDateUpdateInput other = (WorkItemWidgetStartAndDueDateUpdateInput) obj;
        return Objects.equals(dueDate, other.dueDate) && Objects.equals(startDate, other.startDate) && Objects.equals(isFixed, other.isFixed);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetStartAndDueDateUpdateInput [dueDate=" + dueDate + ", startDate=" + startDate + ", isFixed=" + isFixed + "]";
    }

}
