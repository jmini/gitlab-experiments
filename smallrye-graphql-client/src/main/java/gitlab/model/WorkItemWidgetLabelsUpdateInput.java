package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("WorkItemWidgetLabelsUpdateInput")
public class WorkItemWidgetLabelsUpdateInput {

    /**
     * Global IDs of labels to be added to the work item.
     */
    private List<String> addLabelIds;
    /**
     * Global IDs of labels to be removed from the work item.
     */
    private List<String> removeLabelIds;

    public List<String> getAddLabelIds() {
        return addLabelIds;
    }

    public WorkItemWidgetLabelsUpdateInput setAddLabelIds(List<String> addLabelIds) {
        this.addLabelIds = addLabelIds;
        return this;
    }

    public List<String> getRemoveLabelIds() {
        return removeLabelIds;
    }

    public WorkItemWidgetLabelsUpdateInput setRemoveLabelIds(List<String> removeLabelIds) {
        this.removeLabelIds = removeLabelIds;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(addLabelIds, removeLabelIds);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemWidgetLabelsUpdateInput other = (WorkItemWidgetLabelsUpdateInput) obj;
        return Objects.equals(addLabelIds, other.addLabelIds) && Objects.equals(removeLabelIds, other.removeLabelIds);
    }

    @Override
    public String toString() {
        return "WorkItemWidgetLabelsUpdateInput [addLabelIds=" + addLabelIds + ", removeLabelIds=" + removeLabelIds + "]";
    }

}
