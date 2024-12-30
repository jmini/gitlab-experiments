package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

import jakarta.json.bind.annotation.JsonbProperty;

@Name("WorkItemWidgetRolledupDatesInput")
public class WorkItemWidgetRolledupDatesInput {

    /**
     * Fixed start date for the work item.
     */
    @JsonbProperty(value = "startDateFixed", nillable = true)
    private Date startDateFixed;

    /**
     * When start_date_fixed is not provided it defaults to `false`.
     */
    private Boolean startDateIsFixed;

    /**
     * Fixed due date for the work item.
     */
    @JsonbProperty(value = "dueDateFixed", nillable = true)
    private Date dueDateFixed;

    /**
     * When due_date_fixed is not provided it defaults to `false`.
     */
    private Boolean dueDateIsFixed;

    public Date getStartDateFixed() {
        return startDateFixed;
    }

    public WorkItemWidgetRolledupDatesInput setStartDateFixed(Date startDateFixed) {
        this.startDateFixed = startDateFixed;
        return this;
    }

    public Boolean getStartDateIsFixed() {
        return startDateIsFixed;
    }

    public WorkItemWidgetRolledupDatesInput setStartDateIsFixed(Boolean startDateIsFixed) {
        this.startDateIsFixed = startDateIsFixed;
        return this;
    }

    public Date getDueDateFixed() {
        return dueDateFixed;
    }

    public WorkItemWidgetRolledupDatesInput setDueDateFixed(Date dueDateFixed) {
        this.dueDateFixed = dueDateFixed;
        return this;
    }

    public Boolean getDueDateIsFixed() {
        return dueDateIsFixed;
    }

    public WorkItemWidgetRolledupDatesInput setDueDateIsFixed(Boolean dueDateIsFixed) {
        this.dueDateIsFixed = dueDateIsFixed;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDateFixed, startDateIsFixed, dueDateFixed, dueDateIsFixed);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetRolledupDatesInput other = (WorkItemWidgetRolledupDatesInput) obj;
        return Objects.equals(startDateFixed, other.startDateFixed) && Objects.equals(startDateIsFixed, other.startDateIsFixed) && Objects.equals(dueDateFixed, other.dueDateFixed) && Objects.equals(dueDateIsFixed, other.dueDateIsFixed);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetRolledupDatesInput [startDateFixed=" + startDateFixed + ", startDateIsFixed=" + startDateIsFixed + ", dueDateFixed=" + dueDateFixed + ", dueDateIsFixed=" + dueDateIsFixed + "]";
    }

}
