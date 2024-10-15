package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("WorkItemType")
public class WorkItemType {

    /**
     * Global ID of the work item type.
     */
    private String id;
    /**
     * Name of the work item type.
     */
    private String name;

    public String getId() {
        return id;
    }

    public WorkItemType setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public WorkItemType setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemType other = (WorkItemType) obj;
        return Objects.equals(id, other.id) && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "WorkItemType [id=" + id + ", name=" + name + "]";
    }

}
