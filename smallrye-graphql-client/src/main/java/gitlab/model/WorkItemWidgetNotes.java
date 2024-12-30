package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents a notes widget
 */
@Name("WorkItemWidgetNotes")
public class WorkItemWidgetNotes implements WorkItemWidget {

    /**
     * Notes on this work item.
     */
    private DiscussionConnection discussions;

    /**
     * Widget type.
     */
    private WorkItemWidgetType type;

    public DiscussionConnection getDiscussions() {
        return discussions;
    }

    public WorkItemWidgetNotes setDiscussions(DiscussionConnection discussions) {
        this.discussions = discussions;
        return this;
    }

    public WorkItemWidgetType getType() {
        return type;
    }

    public WorkItemWidgetNotes setType(WorkItemWidgetType type) {
        this.type = type;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(discussions, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetNotes other = (WorkItemWidgetNotes) obj;
        return Objects.equals(discussions, other.discussions) && Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetNotes [discussions=" + discussions + ", type=" + type + "]";
    }

}
