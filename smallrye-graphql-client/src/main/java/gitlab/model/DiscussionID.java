package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * A `DiscussionID` is a global ID. It is encoded as a string.
 *
 * An example `DiscussionID` is: `"gid://gitlab/Discussion/1"`.
 */
@Name("DiscussionID")
public class DiscussionID {

    private String value;

    public DiscussionID(String value) {
        this.value = value;
    }

    public static DiscussionID valueOf(String value) {
        return new DiscussionID(value);
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
        DiscussionID other = (DiscussionID) obj;
        return Objects.equals(value, other.value);
    }
}
