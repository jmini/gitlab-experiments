package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents a status widget
 */
@Name("WorkItemWidgetStatus")
public class WorkItemWidgetStatus implements WorkItemWidget {

    /**
     * Status of the work item.
     */
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetStatus other = (WorkItemWidgetStatus) obj;
        return Objects.equals(status, other.status);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetStatus [status=" + status + "]";
    }

}
