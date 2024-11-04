package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("Project")
public class Project {

    /**
     * ID of the project.
     */
    private String id;
    /**
     * Labels available on this project.
     */
    private LabelConnection labels;
    /**
     * Name of the project (without namespace).
     */
    private String name;
    /**
     * Full name of the project with its namespace.
     */
    private String nameWithNamespace;
    /**
     * Namespace of the project.
     */
    private Namespace namespace;
    /**
     * Path of the project.
     */
    private String path;
    /**
     * Web URL of the project.
     */
    private String webUrl;
    /**
     * Work item types available to the project.
     */
    private WorkItemTypeConnection workItemTypes;

    public String getId() {
        return id;
    }

    public Project setId(String id) {
        this.id = id;
        return this;
    }

    public LabelConnection getLabels() {
        return labels;
    }

    public Project setLabels(LabelConnection labels) {
        this.labels = labels;
        return this;
    }

    public String getName() {
        return name;
    }

    public Project setName(String name) {
        this.name = name;
        return this;
    }

    public String getNameWithNamespace() {
        return nameWithNamespace;
    }

    public Project setNameWithNamespace(String nameWithNamespace) {
        this.nameWithNamespace = nameWithNamespace;
        return this;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public Project setNamespace(Namespace namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Project setPath(String path) {
        this.path = path;
        return this;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Project setWebUrl(String webUrl) {
        this.webUrl = webUrl;
        return this;
    }

    public WorkItemTypeConnection getWorkItemTypes() {
        return workItemTypes;
    }

    public Project setWorkItemTypes(WorkItemTypeConnection workItemTypes) {
        this.workItemTypes = workItemTypes;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, labels, name, nameWithNamespace, namespace, path, webUrl, workItemTypes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Project other = (Project) obj;
        return Objects.equals(id, other.id) && Objects.equals(labels, other.labels) && Objects.equals(name, other.name) && Objects.equals(nameWithNamespace, other.nameWithNamespace) && Objects.equals(namespace, other.namespace) && Objects.equals(path, other.path) && Objects.equals(webUrl, other.webUrl) && Objects.equals(workItemTypes, other.workItemTypes);
    }

    @Override
    public String toString() {
        return "Project [id=" + id + ", labels=" + labels + ", name=" + name + ", nameWithNamespace=" + nameWithNamespace + ", namespace=" + namespace + ", path=" + path + ", webUrl=" + webUrl + ", workItemTypes=" + workItemTypes + "]";
    }

}
