package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * The connection type for Note.
 */
@Name("NoteConnection")
public class NoteConnection {

    /**
     * A list of nodes.
     */
    private List<Note> nodes;

    public List<Note> getNodes() {
        return nodes;
    }

    public NoteConnection setNodes(List<Note> nodes) {
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
        NoteConnection other = (NoteConnection) obj;
        return Objects.equals(nodes, other.nodes);
    }

    @Override
    public String toString() {
        return "NoteConnection [nodes=" + nodes + "]";
    }

}
