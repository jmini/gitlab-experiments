package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * The connection type for UserCore.
 */
@Name("UserCoreConnection")
public class UserCoreConnection {

    /**
     * A list of nodes.
     */
    private List<UserCore> nodes;

    public List<UserCore> getNodes() {
        return nodes;
    }

    public void setNodes(List<UserCore> nodes) {
        this.nodes = nodes;
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
        UserCoreConnection other = (UserCoreConnection) obj;
        return Objects.equals(nodes, other.nodes);
    }

    @Override
    public String toString() {
        return "UserCoreConnection [nodes=" + nodes + "]";
    }

}
