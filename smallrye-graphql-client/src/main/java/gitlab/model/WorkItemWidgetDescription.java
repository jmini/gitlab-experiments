package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents a description widget
 */
@Name("WorkItemWidgetDescription")
public class WorkItemWidgetDescription implements WorkItemWidget {

    /**
     * Description of the work item.
     */
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetDescription other = (WorkItemWidgetDescription) obj;
        return Objects.equals(description, other.description);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetDescription [description=" + description + "]";
    }

}
