package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Autogenerated input type of WorkItemUpdate
 */
@Name("WorkItemUpdateInput")
public class WorkItemUpdateInput {

    /**
     * Input for assignees widget.
     */
    private WorkItemWidgetAssigneesInput assigneesWidget;
    /**
     * Input for description widget.
     */
    private WorkItemWidgetDescriptionInput descriptionWidget;
    /**
     * Global ID of the work item.
     */
    private String id;
    /**
     * Input for labels widget.
     */
    private WorkItemWidgetLabelsUpdateInput labelsWidget;
    /**
     * Close or reopen a work item.
     */
    private WorkItemStateEvent stateEvent;
    /**
     * Title of the work item.
     */
    private String title;

    public WorkItemWidgetAssigneesInput getAssigneesWidget() {
        return assigneesWidget;
    }

    public void setAssigneesWidget(WorkItemWidgetAssigneesInput assigneesWidget) {
        this.assigneesWidget = assigneesWidget;
    }

    public WorkItemWidgetDescriptionInput getDescriptionWidget() {
        return descriptionWidget;
    }

    public void setDescriptionWidget(WorkItemWidgetDescriptionInput descriptionWidget) {
        this.descriptionWidget = descriptionWidget;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WorkItemWidgetLabelsUpdateInput getLabelsWidget() {
        return labelsWidget;
    }

    public void setLabelsWidget(WorkItemWidgetLabelsUpdateInput labelsWidget) {
        this.labelsWidget = labelsWidget;
    }

    public WorkItemStateEvent getStateEvent() {
        return stateEvent;
    }

    public void setStateEvent(WorkItemStateEvent stateEvent) {
        this.stateEvent = stateEvent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assigneesWidget, descriptionWidget, id, labelsWidget, stateEvent, title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemUpdateInput other = (WorkItemUpdateInput) obj;
        return Objects.equals(assigneesWidget, other.assigneesWidget) && Objects.equals(descriptionWidget, other.descriptionWidget) && Objects.equals(id, other.id) && Objects.equals(labelsWidget, other.labelsWidget) && Objects.equals(stateEvent, other.stateEvent) && Objects.equals(title, other.title);
    }

    @Override
    public String toString() {
        return "WorkItemUpdateInput [assigneesWidget=" + assigneesWidget + ", descriptionWidget=" + descriptionWidget + ", id=" + id + ", labelsWidget=" + labelsWidget + ", stateEvent=" + stateEvent + ", title=" + title + "]";
    }

}
