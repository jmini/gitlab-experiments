package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents a start and due date widget
 */
@Name("WorkItemWidgetStartAndDueDate")
public class WorkItemWidgetStartAndDueDate implements WorkItemWidget {

    /**
     * Widget type.
     */
    private WorkItemWidgetType type;

    public WorkItemWidgetType getType() {
        return type;
    }

    public WorkItemWidgetStartAndDueDate setType(WorkItemWidgetType type) {
        this.type = type;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
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
        return Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetStartAndDueDate [type=" + type + "]";
    }

}
