package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("WorkItemWidgetDescriptionInput")
public class WorkItemWidgetDescriptionInput {

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
        WorkItemWidgetDescriptionInput other = (WorkItemWidgetDescriptionInput) obj;
        return Objects.equals(description, other.description);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetDescriptionInput [description=" + description + "]";
    }

}
