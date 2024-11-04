package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * The connection type for Discussion.
 */
@Name("DiscussionConnection")
public class DiscussionConnection {

    /**
     * A list of nodes.
     */
    private List<Discussion> nodes;

    public List<Discussion> getNodes() {
        return nodes;
    }

    public DiscussionConnection setNodes(List<Discussion> nodes) {
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
        DiscussionConnection other = (DiscussionConnection) obj;
        return Objects.equals(nodes, other.nodes);
    }

    @Override
    public String toString() {
        return "DiscussionConnection [nodes=" + nodes + "]";
    }

}
