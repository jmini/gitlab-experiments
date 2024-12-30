package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("Group")
public class Group {

    /**
     * Full name of the namespace.
     */
    private String fullName;

    /**
     * Full path of the namespace.
     */
    private String fullPath;

    /**
     * ID of the namespace.
     */
    private String id;

    /**
     * Labels available on this group.
     */
    private LabelConnection labels;

    /**
     * Name of the namespace.
     */
    private String name;

    /**
     * Web URL of the group.
     */
    private String webUrl;

    /**
     * Work item types available to the group.
     */
    private WorkItemTypeConnection workItemTypes;

    public String getFullName() {
        return fullName;
    }

    public Group setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getFullPath() {
        return fullPath;
    }

    public Group setFullPath(String fullPath) {
        this.fullPath = fullPath;
        return this;
    }

    public String getId() {
        return id;
    }

    public Group setId(String id) {
        this.id = id;
        return this;
    }

    public LabelConnection getLabels() {
        return labels;
    }

    public Group setLabels(LabelConnection labels) {
        this.labels = labels;
        return this;
    }

    public String getName() {
        return name;
    }

    public Group setName(String name) {
        this.name = name;
        return this;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Group setWebUrl(String webUrl) {
        this.webUrl = webUrl;
        return this;
    }

    public WorkItemTypeConnection getWorkItemTypes() {
        return workItemTypes;
    }

    public Group setWorkItemTypes(WorkItemTypeConnection workItemTypes) {
        this.workItemTypes = workItemTypes;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, fullPath, id, labels, name, webUrl, workItemTypes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Group other = (Group) obj;
        return Objects.equals(fullName, other.fullName) && Objects.equals(fullPath, other.fullPath) && Objects.equals(id, other.id) && Objects.equals(labels, other.labels) && Objects.equals(name, other.name) && Objects.equals(webUrl, other.webUrl) && Objects.equals(workItemTypes, other.workItemTypes);
    }

    @Override
    public String toString() {
        return "Group [fullName=" + fullName + ", fullPath=" + fullPath + ", id=" + id + ", labels=" + labels + ", name=" + name + ", webUrl=" + webUrl + ", workItemTypes=" + workItemTypes + "]";
    }

}
