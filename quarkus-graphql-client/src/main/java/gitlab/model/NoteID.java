package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * A `NoteID` is a global ID. It is encoded as a string.
 *
 * An example `NoteID` is: `"gid://gitlab/Note/1"`.
 */
@Name("NoteID")
public class NoteID {

    private String value;

    public NoteID(String value) {
        this.value = value;
    }

    public static NoteID valueOf(String value) {
        return new NoteID(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NoteID other = (NoteID) obj;
        return Objects.equals(value, other.value);
    }
}
