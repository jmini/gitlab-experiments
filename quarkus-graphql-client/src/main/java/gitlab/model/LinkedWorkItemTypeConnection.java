package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * The connection type for LinkedWorkItemType.
 */
@Name("LinkedWorkItemTypeConnection")
public class LinkedWorkItemTypeConnection {

    /**
     * A list of nodes.
     */
    private List<LinkedWorkItemType> nodes;

    public List<LinkedWorkItemType> getNodes() {
        return nodes;
    }

    public LinkedWorkItemTypeConnection setNodes(List<LinkedWorkItemType> nodes) {
        this.nodes = nodes;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LinkedWorkItemTypeConnection other = (LinkedWorkItemTypeConnection) obj;
        return Objects.equals(nodes, other.nodes);
    }

    @Override
    public String toString() {
        return "LinkedWorkItemTypeConnection [nodes=" + nodes + "]";
    }

}
