package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("WorkItem")
public class WorkItem {

    /**
     * Whether the work item belongs to an archived project. Always false for group level work items. Introduced in GitLab 16.5: **Status**: Experiment.
     *
     * @deprecated **Status**: Experiment. Introduced in GitLab 16.5.
     */
    @Deprecated
    private Boolean archived;
    /**
     * Timestamp of when the work item was closed.
     */
    private String closedAt;
    /**
     * Indicates the work item is confidential.
     */
    private Boolean confidential;
    /**
     * Timestamp of when the work item was created.
     */
    private String createdAt;
    /**
     * Global ID of the work item.
     */
    private String id;
    /**
     * Internal ID of the work item.
     */
    private String iid;
    /**
     * Lock version of the work item. Incremented each time the work item is updated.
     */
    private Integer lockVersion;
    //    /**
    //     * Name or title of this object.
    //     */
    //    private String name;
    /**
     * Namespace the work item belongs to. Introduced in GitLab 15.10: **Status**: Experiment.
     *
     * @deprecated **Status**: Experiment. Introduced in GitLab 15.10.
     */
    @Deprecated
    private Namespace namespace;
    /**
     * Internal reference of the work item. Returned in shortened format by default.
     */
    private String reference;
    /**
     * State of the work item.
     */
    private WorkItemState state;
    /**
     * Title of the work item.
     */
    private String title;
    /**
     * Timestamp of when the work item was last updated.
     */
    private String updatedAt;
    /**
     * URL of this object.
     */
    private String webUrl;
    /**
     * Collection of widgets that belong to the work item.
     */
    private List<WorkItemWidget> widgets;
    /**
     * Type assigned to the work item.
     */
    private WorkItemType workItemType;

    @Deprecated
    public Boolean getArchived() {
        return archived;
    }

    @Deprecated
    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public Integer getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(Integer lockVersion) {
        this.lockVersion = lockVersion;
    }

    //    public String getName() {
    //        return name;
    //    }
    //
    //    public void setName(String name) {
    //        this.name = name;
    //    }

    @Deprecated
    public Namespace getNamespace() {
        return namespace;
    }

    @Deprecated
    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public WorkItemState getState() {
        return state;
    }

    public void setState(WorkItemState state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public List<WorkItemWidget> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<WorkItemWidget> widgets) {
        this.widgets = widgets;
    }

    public WorkItemType getWorkItemType() {
        return workItemType;
    }

    public void setWorkItemType(WorkItemType workItemType) {
        this.workItemType = workItemType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(archived, closedAt, confidential, createdAt, id, iid, lockVersion, namespace, reference, state, title, updatedAt, webUrl, widgets, workItemType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItem other = (WorkItem) obj;
        return Objects.equals(archived, other.archived) && Objects.equals(closedAt, other.closedAt) && Objects.equals(confidential, other.confidential) && Objects.equals(createdAt, other.createdAt) && Objects.equals(id, other.id) && Objects
                .equals(iid, other.iid) && Objects.equals(lockVersion, other.lockVersion) && Objects.equals(namespace, other.namespace) && Objects.equals(reference, other.reference) && Objects.equals(state, other.state) && Objects.equals(
                        title, other.title) && Objects.equals(updatedAt, other.updatedAt) && Objects.equals(webUrl, other.webUrl) && Objects.equals(widgets, other.widgets) && Objects.equals(workItemType, other.workItemType);
    }

    @Override
    public String toString() {
        return "WorkItem [archived=" + archived + ", closedAt=" + closedAt + ", confidential=" + confidential + ", createdAt=" + createdAt + ", id=" + id + ", iid=" + iid + ", lockVersion=" + lockVersion + ", namespace="
                + namespace + ", reference=" + reference + ", state=" + state + ", title=" + title + ", updatedAt=" + updatedAt + ", webUrl=" + webUrl + ", widgets=" + widgets + ", workItemType=" + workItemType + "]";
    }

}
