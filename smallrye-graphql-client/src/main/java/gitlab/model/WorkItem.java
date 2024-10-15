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
    public WorkItem setArchived(Boolean archived) {
        this.archived = archived;
        return this;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public WorkItem setClosedAt(String closedAt) {
        this.closedAt = closedAt;
        return this;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public WorkItem setConfidential(Boolean confidential) {
        this.confidential = confidential;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public WorkItem setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getId() {
        return id;
    }

    public WorkItem setId(String id) {
        this.id = id;
        return this;
    }

    public String getIid() {
        return iid;
    }

    public WorkItem setIid(String iid) {
        this.iid = iid;
        return this;
    }

    public Integer getLockVersion() {
        return lockVersion;
    }

    public WorkItem setLockVersion(Integer lockVersion) {
        this.lockVersion = lockVersion;
        return this;
    }

    @Deprecated
    public Namespace getNamespace() {
        return namespace;
    }

    @Deprecated
    public WorkItem setNamespace(Namespace namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public WorkItem setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public WorkItemState getState() {
        return state;
    }

    public WorkItem setState(WorkItemState state) {
        this.state = state;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public WorkItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public WorkItem setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public WorkItem setWebUrl(String webUrl) {
        this.webUrl = webUrl;
        return this;
    }

    public List<WorkItemWidget> getWidgets() {
        return widgets;
    }

    public WorkItem setWidgets(List<WorkItemWidget> widgets) {
        this.widgets = widgets;
        return this;
    }

    public WorkItemType getWorkItemType() {
        return workItemType;
    }

    public WorkItem setWorkItemType(WorkItemType workItemType) {
        this.workItemType = workItemType;
        return this;
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
        return Objects.equals(archived, other.archived) && Objects.equals(closedAt, other.closedAt) && Objects.equals(confidential, other.confidential) && Objects.equals(createdAt, other.createdAt) && Objects.equals(id, other.id) && Objects.equals(iid, other.iid) && Objects.equals(lockVersion, other.lockVersion) && Objects.equals(namespace, other.namespace) && Objects.equals(reference, other.reference) && Objects.equals(state, other.state) && Objects.equals(title, other.title) && Objects.equals(updatedAt, other.updatedAt) && Objects.equals(webUrl, other.webUrl) && Objects.equals(widgets, other.widgets) && Objects.equals(workItemType, other.workItemType);
    }

    @Override
    public String toString() {
        return "WorkItem [archived=" + archived + ", closedAt=" + closedAt + ", confidential=" + confidential + ", createdAt=" + createdAt + ", id=" + id + ", iid=" + iid + ", lockVersion=" + lockVersion + ", namespace=" + namespace + ", reference=" + reference + ", state=" + state + ", title=" + title + ", updatedAt=" + updatedAt + ", webUrl=" + webUrl + ", widgets=" + widgets + ", workItemType=" + workItemType + "]";
    }

}
