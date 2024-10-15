package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("WorkItemRef")
//Duplicate of `WorkItem` to avoid SRGQLDC035008: Field recursion found
public class WorkItemRef {

    /**
     * Whether the work item belongs to an archived project. Always false for group level work items. Introduced in GitLab 16.5: **Status**: Experiment.
     *
     * @deprecated **Status**: Experiment. Introduced in GitLab 16.5.
     */
    @Deprecated
    private Boolean archived;
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
     * URL of this object.
     */
    private String webUrl;
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

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public WorkItemType getWorkItemType() {
        return workItemType;
    }

    public void setWorkItemType(WorkItemType workItemType) {
        this.workItemType = workItemType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(archived, confidential, createdAt, id, iid, namespace, reference, state, title, webUrl, workItemType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemRef other = (WorkItemRef) obj;
        return Objects.equals(archived, other.archived) && Objects.equals(confidential, other.confidential) && Objects.equals(createdAt, other.createdAt) && Objects.equals(id, other.id) && Objects.equals(iid, other.iid) && Objects.equals(
                namespace, other.namespace) && Objects.equals(reference, other.reference) && state == other.state && Objects.equals(title, other.title) && Objects.equals(webUrl, other.webUrl) && Objects
                        .equals(workItemType, other.workItemType);
    }

    @Override
    public String toString() {
        return "WorkItemRef [archived=" + archived + ", confidential=" + confidential + ", createdAt=" + createdAt + ", id=" + id + ", iid=" + iid + ", namespace=" + namespace + ", reference=" + reference + ", state="
                + state + ", title=" + title + ", webUrl=" + webUrl + ", workItemType=" + workItemType + "]";
    }
}
