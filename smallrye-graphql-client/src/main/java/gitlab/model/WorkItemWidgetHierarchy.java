package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents a hierarchy widget
 */
@Name("WorkItemWidgetHierarchy")
public class WorkItemWidgetHierarchy implements WorkItemWidget {

    /**
     * Ancestors (parents) of the work item.
     */
    private WorkItemConnectionRef ancestors;
    /**
     * Child work items.
     */
    private WorkItemConnectionRef children;
    /**
     * Parent work item.
     */
    private WorkItemRef parent;

    public WorkItemConnectionRef getAncestors() {
        return ancestors;
    }

    public void setAncestors(WorkItemConnectionRef ancestors) {
        this.ancestors = ancestors;
    }

    public WorkItemConnectionRef getChildren() {
        return children;
    }

    public void setChildren(WorkItemConnectionRef children) {
        this.children = children;
    }

    public WorkItemRef getParent() {
        return parent;
    }

    public void setParent(WorkItemRef parent) {
        this.parent = parent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ancestors, children, parent);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetHierarchy other = (WorkItemWidgetHierarchy) obj;
        return Objects.equals(ancestors, other.ancestors) && Objects.equals(children, other.children) && Objects.equals(parent, other.parent);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetHierarchy [ancestors=" + ancestors + ", children=" + children + ", parent=" + parent + "]";
    }

}
