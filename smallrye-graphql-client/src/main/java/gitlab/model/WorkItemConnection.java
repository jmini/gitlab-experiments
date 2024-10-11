package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * The connection type for WorkItem.
 */
@Name("WorkItemConnection")
public class WorkItemConnection {

    /**
     * Total count of collection.
     */
    private Integer count;
    /**
     * A list of nodes.
     */
    private List<WorkItem> nodes;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<WorkItem> getNodes() {
        return nodes;
    }

    public void setNodes(List<WorkItem> nodes) {
        this.nodes = nodes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, nodes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemConnection other = (WorkItemConnection) obj;
        return Objects.equals(count, other.count) && Objects.equals(nodes, other.nodes);
    }

    @Override
    public String toString() {
        return "WorkItemConnection [count=" + count + ", nodes=" + nodes + "]";
    }

}
