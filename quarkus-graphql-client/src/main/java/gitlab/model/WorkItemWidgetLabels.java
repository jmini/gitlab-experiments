package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents the labels widget
 */
@Name("WorkItemWidgetLabels")
public class WorkItemWidgetLabels implements WorkItemWidget {

    /**
     * Labels assigned to the work item.
     */
    private LabelConnection labels;
    /**
     * Widget type.
     */
    private WorkItemWidgetType type;

    public LabelConnection getLabels() {
        return labels;
    }

    public WorkItemWidgetLabels setLabels(LabelConnection labels) {
        this.labels = labels;
        return this;
    }

    public WorkItemWidgetType getType() {
        return type;
    }

    public WorkItemWidgetLabels setType(WorkItemWidgetType type) {
        this.type = type;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(labels, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetLabels other = (WorkItemWidgetLabels) obj;
        return Objects.equals(labels, other.labels) && Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetLabels [labels=" + labels + ", type=" + type + "]";
    }

}
