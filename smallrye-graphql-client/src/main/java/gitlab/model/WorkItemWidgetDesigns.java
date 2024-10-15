package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents designs widget
 */
@Name("WorkItemWidgetDesigns")
public class WorkItemWidgetDesigns implements WorkItemWidget {

    /**
     * Widget type.
     */
    private WorkItemWidgetType type;

    public WorkItemWidgetType getType() {
        return type;
    }

    public WorkItemWidgetDesigns setType(WorkItemWidgetType type) {
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
        WorkItemWidgetDesigns other = (WorkItemWidgetDesigns) obj;
        return Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetDesigns [type=" + type + "]";
    }

}
