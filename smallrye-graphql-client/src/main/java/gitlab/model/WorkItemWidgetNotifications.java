package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents the notifications widget
 */
@Name("WorkItemWidgetNotifications")
public class WorkItemWidgetNotifications implements WorkItemWidget {

    /**
     * Widget type.
     */
    private WorkItemWidgetType type;

    public WorkItemWidgetType getType() {
        return type;
    }

    public WorkItemWidgetNotifications setType(WorkItemWidgetType type) {
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
        WorkItemWidgetNotifications other = (WorkItemWidgetNotifications) obj;
        return Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetNotifications [type=" + type + "]";
    }

}
