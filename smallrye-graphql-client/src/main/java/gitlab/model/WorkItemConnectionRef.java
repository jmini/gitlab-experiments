package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * The connection type for WorkItem.
 */
@Name("WorkItemConnectionRef")
//Duplicate of `WorkItemConnection` to avoid SRGQLDC035008: Field recursion found
public class WorkItemConnectionRef {

    /**
     * Total count of collection.
     */
    private Integer count;
    /**
     * A list of nodes.
     */
    private List<WorkItemRef> nodes;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<WorkItemRef> getNodes() {
        return nodes;
    }

    public void setNodes(List<WorkItemRef> nodes) {
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
        WorkItemConnectionRef other = (WorkItemConnectionRef) obj;
        return Objects.equals(count, other.count) && Objects.equals(nodes, other.nodes);
    }

    @Override
    public String toString() {
        return "WorkItemConnection [count=" + count + ", nodes=" + nodes + "]";
    }

}
