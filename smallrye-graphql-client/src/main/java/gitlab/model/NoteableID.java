package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * A `NoteableID` is a global ID. It is encoded as a string.
 *
 * An example `NoteableID` is: `"gid://gitlab/Noteable/1"`.
 */
@Name("NoteableID")
public class NoteableID {

    private String value;

    public NoteableID(String value) {
        this.value = value;
    }

    public static NoteableID valueOf(String value) {
        return new NoteableID(value);
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
        NoteableID other = (NoteableID) obj;
        return Objects.equals(value, other.value);
    }
}
