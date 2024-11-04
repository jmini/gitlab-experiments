package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * A `LabelID` is a global ID. It is encoded as a string.
 *
 * An example `LabelID` is: `"gid://gitlab/Label/1"`.
 */
@Name("LabelID")
public class LabelID {

    private String value;

    public LabelID(String value) {
        this.value = value;
    }

    public static LabelID valueOf(String value) {
        return new LabelID(value);
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
        LabelID other = (LabelID) obj;
        return Objects.equals(value, other.value);
    }
}
