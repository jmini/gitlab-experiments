package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("Namespace")
public class Namespace {

    /**
     * Full path of the namespace.
     */
    private String fullPath;
    /**
     * ID of the namespace.
     */
    private String id;
    /**
     * Visibility of the namespace.
     */
    private String visibility;
    /**
     * Work item types available to the namespace. Introduced in GitLab 17.2: **Status**: Experiment.
     *
     * @deprecated **Status**: Experiment. Introduced in GitLab 17.2.
     */
    @Deprecated
    private WorkItemTypeConnection workItemTypes;

    public String getFullPath() {
        return fullPath;
    }

    public Namespace setFullPath(String fullPath) {
        this.fullPath = fullPath;
        return this;
    }

    public String getId() {
        return id;
    }

    public Namespace setId(String id) {
        this.id = id;
        return this;
    }

    public String getVisibility() {
        return visibility;
    }

    public Namespace setVisibility(String visibility) {
        this.visibility = visibility;
        return this;
    }

    @Deprecated
    public WorkItemTypeConnection getWorkItemTypes() {
        return workItemTypes;
    }

    @Deprecated
    public Namespace setWorkItemTypes(WorkItemTypeConnection workItemTypes) {
        this.workItemTypes = workItemTypes;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullPath, id, visibility, workItemTypes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Namespace other = (Namespace) obj;
        return Objects.equals(fullPath, other.fullPath) && Objects.equals(id, other.id) && Objects.equals(visibility, other.visibility) && Objects.equals(workItemTypes, other.workItemTypes);
    }

    @Override
    public String toString() {
        return "Namespace [fullPath=" + fullPath + ", id=" + id + ", visibility=" + visibility + ", workItemTypes=" + workItemTypes + "]";
    }

}
